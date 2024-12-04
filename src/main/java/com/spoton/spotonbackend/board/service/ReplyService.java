package com.spoton.spotonbackend.board.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spoton.spotonbackend.board.dto.request.ReqReplyCreateDto;
import com.spoton.spotonbackend.board.dto.request.ReqReplyModifyDto;
import com.spoton.spotonbackend.board.dto.response.ResReplyDto;
import com.spoton.spotonbackend.board.entity.*;
import com.spoton.spotonbackend.board.repository.BoardRepository;
import com.spoton.spotonbackend.board.repository.ReplyLikeRepository;
import com.spoton.spotonbackend.board.repository.ReplyRepository;
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

    private final JPAQueryFactory queryFactory;
    private final ReplyRepository replyRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final ReplyLikeRepository replyLikeRepository;

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

    public void delete(Long replyId, TokenUserInfo userInfo) {
        Reply reply = replyRepository.findById(replyId).orElseThrow(
                () -> new EntityNotFoundException("댓글을 찾을 수 없습니다.")
        );

        User user = userRepository.findByEmail(userInfo.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("작성자 정보를 찾을 수 없습니다.")
        );

        if (!reply.getUser().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        replyRepository.delete(reply);
    }

    public Reply increaseReportCount(Long replyId) {
        Reply reply = replyRepository.findById(replyId).orElseThrow(
                () -> new EntityNotFoundException("댓글을 찾을 수 없습니다.")
        );

        Long reportCount = reply.getReportCount();
        reportCount += 1;

        reply.setReportCount(reportCount);

        return reply;
    }

    public ReplyLike addLikeCount(Long replyId, TokenUserInfo userInfo) {

        User user = userRepository.findByEmail(userInfo.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("회원 정보를 찾을 수 없습니다.")
        );

        Reply reply = replyRepository.findById(replyId).orElseThrow(
                () -> new EntityNotFoundException("댓글을 찾을 수 없습니다.")
        );

        if (replyLikeRepository.existsByUser_UserIdAndReply_ReplyId(user.getUserId(), replyId)) {
            throw new IllegalStateException("이미 좋아요를 한 유저입니다.");
        }

        ReplyLike replyLike = new ReplyLike();
        replyLike.setReply(reply);
        replyLike.setUser(user);

        return replyLikeRepository.save(replyLike);
    }

    public void cancelLikeCount(Long replyId, TokenUserInfo userInfo) {
        User user = userRepository.findByEmail(userInfo.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("회원 정보를 찾을 수 없습니다.")
        );

        ReplyLike replyLike = replyLikeRepository.findByUser_UserIdAndReply_ReplyId(user.getUserId(), replyId).orElseThrow(
                () -> new EntityNotFoundException("좋아요 로그를 찾을 수 없습니다.")
        );

        replyLikeRepository.delete(replyLike);
    }
}
