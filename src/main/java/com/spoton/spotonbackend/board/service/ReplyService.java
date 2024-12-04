package com.spoton.spotonbackend.board.service;

import com.spoton.spotonbackend.board.dto.request.ReqReplyCreateDto;
import com.spoton.spotonbackend.board.dto.request.ReqReplyModifyDto;
import com.spoton.spotonbackend.board.entity.Reply;
import com.spoton.spotonbackend.board.entity.ReplyLike;
import com.spoton.spotonbackend.board.repository.ReplyRepository;
import com.spoton.spotonbackend.common.auth.TokenUserInfo;
import com.spoton.spotonbackend.user.service.UserService;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final UserService userService;

    public Page<Reply> list(Long boardId, Pageable pageable) {


    }

    public Reply create(ReqReplyCreateDto dto, TokenUserInfo userInfo) {


    }

    public Reply modify(ReqReplyModifyDto dto, TokenUserInfo userInfo) {

    }

    public void delete(Long replyId, TokenUserInfo userInfo) {

    }

    public Reply increaseReportCount(Long replyId) {

    }

    public ReplyLike addLikeCount(Long replyId, TokenUserInfo userInfo) {

    }

    public void cancelLikeCount(Long replyId, TokenUserInfo userInfo) {

    }
}
