package com.spoton.spotonbackend.board.controller;

import com.spoton.spotonbackend.board.dto.request.ReqReplyCreateDto;
import com.spoton.spotonbackend.board.dto.request.ReqReplyModifyDto;
import com.spoton.spotonbackend.board.dto.request.ReqReplyReportDto;
import com.spoton.spotonbackend.board.entity.Reply;
import com.spoton.spotonbackend.board.entity.ReplyLike;
import com.spoton.spotonbackend.board.service.ReplyService;
import com.spoton.spotonbackend.common.auth.EmailProvider;
import com.spoton.spotonbackend.common.auth.TokenUserInfo;
import com.spoton.spotonbackend.common.dto.CommonErrorDto;
import com.spoton.spotonbackend.common.dto.CommonResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reply")
@RequiredArgsConstructor
@Slf4j
public class ReplyController {

    private final ReplyService replyService;
    private final EmailProvider emailProvider;

    // 댓글 조회
    @GetMapping("/list")
    public ResponseEntity<?> replyList(@RequestParam Long boardId, Pageable pageable){

        Page<Reply> replies = replyService.list(boardId, pageable);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "댓글 조회 완료", replies);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    // 댓글 작성
    @PostMapping("/create")
    public ResponseEntity<?> replyCreate(@RequestBody ReqReplyCreateDto dto,
                                         @AuthenticationPrincipal TokenUserInfo userInfo){

        Reply reply = replyService.create(dto, userInfo);

        CommonResDto resDto = new CommonResDto(HttpStatus.CREATED, "댓글 등록 완료", reply.getReplyId());
        return new ResponseEntity<>(resDto, HttpStatus.CREATED);
    }

    // 댓글 수정
    @PatchMapping("/modify")
    public ResponseEntity<?> replyModify(@RequestParam ReqReplyModifyDto dto,
                                         @AuthenticationPrincipal TokenUserInfo userInfo) {

        Reply reply = replyService.modify(dto, userInfo);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "댓글 수정 완료", reply.getReplyId());
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    // 댓글 삭제
    @PostMapping("/delete")
    public ResponseEntity<?> replyDelete(@RequestParam Long replyId,
                                         @AuthenticationPrincipal TokenUserInfo userInfo) {

        replyService.delete(replyId, userInfo);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "댓글 삭제 완료", true);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    // 댓글 신고 (관리자에게 내역 메일 보내기)
    @PostMapping("/report")
    public ResponseEntity<?> sendReplyReport(@RequestBody ReqReplyReportDto dto,
                                             @AuthenticationPrincipal TokenUserInfo userInfo) {

        String result = emailProvider.sendReportMail(dto.getReplyId(), dto.getReportContent(), userInfo, "댓글");
        if (result.equals("fail")) {
            CommonErrorDto errorDto = new CommonErrorDto(HttpStatus.SERVICE_UNAVAILABLE, "신고 실패, 다시 확인해주세요.");
            return new ResponseEntity<>(errorDto, HttpStatus.SERVICE_UNAVAILABLE);
        }

        // 신고 카운트 증가
        Reply reply = replyService.increaseReportCount(dto.getReplyId());

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "댓글 신고 성공", reply.getReplyId());
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    // 좋아요 추가
    @PostMapping("/like_add")
    public ResponseEntity<?> addLikeCount(@RequestParam Long replyId,
                                          @AuthenticationPrincipal TokenUserInfo userInfo) {

        // 좋아요 증가
        ReplyLike replyLike = replyService.addLikeCount(replyId, userInfo);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "좋아요 추가 성공", replyLike);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    // 좋아요 삭제
    @PostMapping("/list_calcel")
    public ResponseEntity<?> cancelLikeCount(@RequestParam Long replyId,
                                             @AuthenticationPrincipal TokenUserInfo userInfo) {

        // 좋아요 삭제
        replyService.cancelLikeCount(replyId, userInfo);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "좋아요 삭제 성공", true);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }
}
