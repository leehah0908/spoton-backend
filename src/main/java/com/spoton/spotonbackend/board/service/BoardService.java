package com.spoton.spotonbackend.board.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spoton.spotonbackend.board.dto.request.ReqBoardCreateDto;
import com.spoton.spotonbackend.board.dto.request.ReqBoardModifyDto;
import com.spoton.spotonbackend.board.dto.request.ReqBoardReportDto;
import com.spoton.spotonbackend.board.dto.response.ResBoardDto;
import com.spoton.spotonbackend.board.entity.Board;
import com.spoton.spotonbackend.board.entity.BoardLike;
import com.spoton.spotonbackend.board.entity.BoardReport;
import com.spoton.spotonbackend.board.repository.BoardLikeRepository;
import com.spoton.spotonbackend.board.repository.BoardReportRepository;
import com.spoton.spotonbackend.board.repository.BoardRepository;
import com.spoton.spotonbackend.common.auth.EmailProvider;
import com.spoton.spotonbackend.common.auth.TokenUserInfo;
import com.spoton.spotonbackend.common.dto.CommonErrorDto;
import com.spoton.spotonbackend.game.dto.response.ResGameDto;
import com.spoton.spotonbackend.user.entity.User;
import com.spoton.spotonbackend.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.spoton.spotonbackend.board.entity.QBoard.*;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final BoardReportRepository boardReportRepository;

    private final JPAQueryFactory queryFactory;
    private final EmailProvider emailProvider;

    public Board create(ReqBoardCreateDto dto, TokenUserInfo userInfo) {

        User user = userRepository.findByEmail(userInfo.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("작성자 정보를 찾을 수 없습니다.")
        );

        Board board = dto.toBoard();
        board.setUser(user);

        return boardRepository.save(board);
    }

    public Page<ResBoardDto> list(String searchType,
                                  String searchKeyword,
                                  Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();

        // 쿼리 조각 만들기
        if (searchKeyword != null) {
            switch (searchType) {
                case "writer" -> builder.and(board.user.nickname.like("%" + searchKeyword + "%"));
                case "subject" -> builder.and(board.subject.like("%" + searchKeyword + "%"));
                case "content" -> builder.and(board.content.like("%" + searchKeyword + "%"));
            }
        }

        // 검색 및 페이징 처리
        List<Board> rawBoards = queryFactory
                .selectFrom(board)
                .where(builder)
                .orderBy(board.createTime.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // page 객체로 만들기 위해 총 검색 결과 수
        long total = queryFactory
                .selectFrom(board)
                .where(builder)
                .fetchCount();

        // page 객체 생성.
        Page<Board> boards = new PageImpl<>(rawBoards, pageable, total);

        return boards.map(Board::toResBoardDto);
    }

    public ResBoardDto boardDetail(Long boardId) {

        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new EntityNotFoundException("게시물 정보를 찾을 수 없습니다.")
        );

        return board.toResBoardDto();
    }

    public Board modify(ReqBoardModifyDto dto, TokenUserInfo userInfo) {

        Board board = boardRepository.findById(dto.getBoardId()).orElseThrow(
                () -> new EntityNotFoundException("게시물을 찾을 수 없습니다.")
        );

        User user = userRepository.findByEmail(userInfo.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("작성자 정보를 찾을 수 없습니다.")
        );

        if (!board.getUser().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        board.setSubject(dto.getSubject());
        board.setContent(dto.getContent());
        board.setSports(dto.getSports());

        return board;
    }

    public void delete(Long boardId, TokenUserInfo userInfo) {

        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new EntityNotFoundException("게시물을 찾을 수 없습니다.")
        );

        User user = userRepository.findByEmail(userInfo.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("작성자 정보를 찾을 수 없습니다.")
        );

        if (!board.getUser().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        boardRepository.delete(board);
    }

    public String sendReport(ReqBoardReportDto dto, TokenUserInfo userInfo) {

        Board board = boardRepository.findById(dto.getBoardId()).orElseThrow(
                () -> new EntityNotFoundException("게시물을 찾을 수 없습니다.")
        );

        User user = userRepository.findByEmail(userInfo.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("회원 정보를 찾을 수 없습니다.")
        );

        if (!boardReportRepository.existsByUser_UserIdAndBoard_BoardId(user.getUserId(), dto.getBoardId())) {

            // 관리자에게 신고 내역 보내기
            String result = emailProvider.sendReportMail(dto.getBoardId(), dto.getReportContent(), userInfo, "게시물");
            if (result.equals("fail")) {
                return "email send fail";
            }

            BoardReport boardReport = new BoardReport();
            boardReport.setBoard(board);
            boardReport.setUser(user);

            boardReportRepository.save(boardReport);
            board.setReportCount(board.getReportCount() + 1);

            return "success";
        }
        return "existed";
    }

    public Board increaseViewCount(Long boardId) {

        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new EntityNotFoundException("게시물을 찾을 수 없습니다.")
        );

        Long viewCount = board.getViewCount();
        viewCount += 1;

        board.setViewCount(viewCount);

        return board;
    }

    public void likeCount(Long boardId, TokenUserInfo userInfo) {

        User user = userRepository.findByEmail(userInfo.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("회원 정보를 찾을 수 없습니다.")
        );

        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new EntityNotFoundException("게시물을 찾을 수 없습니다.")
        );

        if (boardLikeRepository.existsByUser_UserIdAndBoard_BoardId(user.getUserId(), boardId)) {
            BoardLike boardLike = boardLikeRepository.findByUser_UserIdAndBoard_BoardId(user.getUserId(), boardId).orElseThrow(
                    () -> new EntityNotFoundException("좋아요 로그를 찾을 수 없습니다.")
            );

            boardLikeRepository.delete(boardLike);
            board.setLikeCount(board.getLikeCount() - 1);
        } else {
            BoardLike boardLike = new BoardLike();
            boardLike.setBoard(board);
            boardLike.setUser(user);

            boardLikeRepository.save(boardLike);
            board.setLikeCount(board.getLikeCount() + 1);
        }
    }

    public List<ResBoardDto> hotBoard() {

        List<Board> hotBoards = boardRepository.findTop10ByOrderByLikeCountDesc();

        return hotBoards.stream().map(Board::toResBoardDto).collect(Collectors.toList());

    }
}
