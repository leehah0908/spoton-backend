package com.spoton.spotonbackend.board.controller;

import com.spoton.spotonbackend.board.service.ReplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reply")
@RequiredArgsConstructor
@Slf4j
public class ReplyController {

    private final ReplyService replyService;

    // 댓글 조회
//    @PostMapping("/list")

    // 댓글 작성
//    @PostMapping("/list")

    // 댓글 수정
//    @PostMapping("/list")

    // 댓글 삭제
//    @PostMapping("/list")

    // 댓글 신고
//    @PostMapping("/list")
}
