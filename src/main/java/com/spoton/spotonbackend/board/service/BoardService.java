package com.spoton.spotonbackend.board.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spoton.spotonbackend.board.dto.request.ReqBoardCreateDto;
import com.spoton.spotonbackend.board.dto.request.ReqBoardModifyDto;
import com.spoton.spotonbackend.board.dto.request.ReqBoardSearchDto;
import com.spoton.spotonbackend.board.dto.response.ResBoardDto;
import com.spoton.spotonbackend.board.entity.Board;
import com.spoton.spotonbackend.board.entity.BoardLike;
import com.spoton.spotonbackend.board.repository.BoardLikeRepository;
import com.spoton.spotonbackend.board.repository.BoardRepository;
import com.spoton.spotonbackend.common.auth.TokenUserInfo;
import com.spoton.spotonbackend.user.entity.User;
import com.spoton.spotonbackend.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.spoton.spotonbackend.board.entity.QBoard.*;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

    private final JPAQueryFactory queryFactory;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final BoardLikeRepository boardLikeRepository;

    public Board create(ReqBoardCreateDto dto, TokenUserInfo userInfo) {

        User user = userRepository.findByEmail(userInfo.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("회원 정보를 찾을 수 없습니다.")
        );

        Board board = dto.toBoard();
        board.setUser(user);

        return boardRepository.save(board);
    }

    public Page<ResBoardDto> list(ReqBoardSearchDto dto, Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();

        // 쿼리 조각 만들기
        if (dto.getSearchKeyword() != null) {
            switch (dto.getSearchType()) {
                case "writer" -> builder.and(board.user.nickname.like("%" + dto.getSearchKeyword() + "%"));
                case "subject" -> builder.and(board.subject.like("%" + dto.getSearchKeyword() + "%"));
                case "content" -> builder.and(board.content.like("%" + dto.getSearchKeyword() + "%"));
            }
        }

        // 검색 및 페이징 처리
        List<Board> rawProducts = queryFactory
                .selectFrom(board)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // page 객체로 만들기 위해 총 검색 결과 수
        long total = queryFactory
                .selectFrom(board)
                .where(builder)
                .fetchCount();

        // page 객체 생성.
        Page<Board> boards = new PageImpl<>(rawProducts, pageable, total);

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
        board.setLeagueId(dto.getLeagueId());

        return board;
    }

    public void delete(ReqBoardModifyDto dto, TokenUserInfo userInfo) {

        Board board = boardRepository.findById(dto.getBoardId()).orElseThrow(
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

    public Board increaseReportCount(Long boardId) {

        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new EntityNotFoundException("게시물을 찾을 수 없습니다.")
        );

        Long reportCount = board.getReportCount();
        reportCount += 1;

        board.setReportCount(reportCount);

        return board;
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

    public BoardLike addLikeCount(Long boardId, TokenUserInfo userInfo) {

        User user = userRepository.findByEmail(userInfo.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("회원 정보를 찾을 수 없습니다.")
        );

        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new EntityNotFoundException("게시물을 찾을 수 없습니다.")
        );

        if (boardLikeRepository.existsByUserIdAndBoardId(user.getUserId(), boardId)) {
            throw new IllegalStateException("이미 좋아요를 한 유저입니다.");
        }

        BoardLike boardLike = new BoardLike();
        boardLike.setBoard(board);
        boardLike.setUser(user);

        return boardLikeRepository.save(boardLike);
    }

    public void cancelLikeCount(Long boardId, TokenUserInfo userInfo) {

        User user = userRepository.findByEmail(userInfo.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("회원 정보를 찾을 수 없습니다.")
        );

        BoardLike boardLike = boardLikeRepository.findByUserIdAndBoardId(user.getUserId(), boardId).orElseThrow(
                () -> new EntityNotFoundException("좋아요 로그를 찾을 수 없습니다.")
        );

        boardLikeRepository.delete(boardLike);
    }
}
