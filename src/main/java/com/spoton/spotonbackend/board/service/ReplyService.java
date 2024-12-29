package com.spoton.spotonbackend.board.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spoton.spotonbackend.board.dto.request.ReqReplyCreateDto;
import com.spoton.spotonbackend.board.dto.request.ReqReplyModifyDto;
import com.spoton.spotonbackend.board.dto.request.ReqReplyReportDto;
import com.spoton.spotonbackend.board.dto.response.ResReplyDto;
import com.spoton.spotonbackend.board.entity.*;
import com.spoton.spotonbackend.board.repository.BoardRepository;
import com.spoton.spotonbackend.board.repository.ReplyLikeRepository;
import com.spoton.spotonbackend.board.repository.ReplyReportRepository;
import com.spoton.spotonbackend.board.repository.ReplyRepository;
import com.spoton.spotonbackend.common.auth.EmailProvider;
import com.spoton.spotonbackend.common.auth.TokenUserInfo;
import com.spoton.spotonbackend.user.entity.User;
import com.spoton.spotonbackend.user.repository.UserRepository;
import com.spoton.spotonbackend.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.spoton.spotonbackend.board.entity.QBoard.board;
import static com.spoton.spotonbackend.board.entity.QReply.reply;

@Service
@RequiredArgsConstructor
@Transactional
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final ReplyLikeRepository replyLikeRepository;
    private final ReplyReportRepository replyReportRepository;

    private final JPAQueryFactory queryFactory;
    private final EmailProvider emailProvider;

    public Page<ResReplyDto> list(Long boardId, Pageable pageable) {

        List<Reply> rawReplies = queryFactory
                .selectFrom(reply)
                .where(reply.board.boardId.eq(boardId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .selectFrom(reply)
                .where(reply.board.boardId.eq(boardId))
                .fetchCount();

        Page<Reply> replies = new PageImpl<>(rawReplies, pageable, total);

        return replies.map(Reply::toResReplyDto);
    }

    public Reply create(ReqReplyCreateDto dto, TokenUserInfo userInfo) {

        Board board = boardRepository.findById(dto.getBoardId()).orElseThrow(
                () -> new EntityNotFoundException("게시물을 찾을 수 없습니다.")
        );

        User user = userRepository.findByEmail(userInfo.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("작성자 정보를 찾을 수 없습니다.")
        );

        Reply reply = new Reply();
        reply.setContent(dto.getContent());
        reply.setUser(user);
        reply.setBoard(board);

        board.setReplyCount(board.getReplyCount() + 1);

        return replyRepository.save(reply);
    }

    public Reply modify(ReqReplyModifyDto dto, TokenUserInfo userInfo) {
        Reply reply = replyRepository.findById(dto.getReplyId()).orElseThrow(
                () -> new EntityNotFoundException("댓글을 찾을 수 없습니다.")
        );

        User user = userRepository.findByEmail(userInfo.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("작성자 정보를 찾을 수 없습니다.")
        );

        if (!reply.getUser().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        reply.setContent(dto.getContent());

        return reply;
    }

    public void delete(Long replyId, Long boardId, TokenUserInfo userInfo) {
        Reply reply = replyRepository.findById(replyId).orElseThrow(
                () -> new EntityNotFoundException("댓글을 찾을 수 없습니다.")
        );

        User user = userRepository.findByEmail(userInfo.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("작성자 정보를 찾을 수 없습니다.")
        );

        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new EntityNotFoundException("게시물을 찾을 수 없습니다.")
        );

        if (!reply.getUser().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        replyRepository.delete(reply);
        board.setReplyCount(board.getReplyCount() - 1);
    }

    public String sendReport(ReqReplyReportDto dto, TokenUserInfo userInfo) {

        Reply reply = replyRepository.findById(dto.getReplyId()).orElseThrow(
                () -> new EntityNotFoundException("댓글을 찾을 수 없습니다.")
        );

        User user = userRepository.findByEmail(userInfo.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("회원 정보를 찾을 수 없습니다.")
        );

        if(!replyReportRepository.existsByUser_UserIdAndReply_ReplyId(user.getUserId(), dto.getReplyId())){

            // 관리자에게 신고 내역 보내기
            String result = emailProvider.sendReportMail(dto.getReplyId(), dto.getReportContent(), userInfo, "댓글");
            if (result.equals("fail")) {
                return "email send fail";
            }

            ReplyReport replyReport = new ReplyReport();
            replyReport.setUser(user);
            replyReport.setReply(reply);

            replyReportRepository.save(replyReport);
            reply.setReportCount(reply.getReportCount() + 1);

            return "success";
        }
        return "existed";
    }

    public void likeCount(Long replyId, TokenUserInfo userInfo) {

        User user = userRepository.findByEmail(userInfo.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("회원 정보를 찾을 수 없습니다.")
        );

        Reply reply = replyRepository.findById(replyId).orElseThrow(
                () -> new EntityNotFoundException("댓글을 찾을 수 없습니다.")
        );

        if (replyLikeRepository.existsByUser_UserIdAndReply_ReplyId(user.getUserId(), replyId)) {
            ReplyLike replyLike = replyLikeRepository.findByUser_UserIdAndReply_ReplyId(user.getUserId(), replyId).orElseThrow(
                    () -> new EntityNotFoundException("좋아요 로그를 찾을 수 없습니다.")
            );

            replyLikeRepository.delete(replyLike);
            reply.setLikeCount(reply.getLikeCount() - 1);
        } else {
            ReplyLike replyLike = new ReplyLike();
            replyLike.setReply(reply);
            replyLike.setUser(user);

            replyLikeRepository.save(replyLike);
            reply.setLikeCount(reply.getLikeCount() + 1);
        }
    }

    public List<String> replyLikeList(Long replyId) {

        List<ReplyLike> replyLikes = replyLikeRepository.findByReply_ReplyId(replyId).orElseThrow(
                () -> new EntityNotFoundException("이 댓글의 좋아요 명단을 찾을 수 없음.")
        );

        return replyLikes.stream().map(replyLike -> replyLike.getUser().getEmail()).toList();
    }
}
