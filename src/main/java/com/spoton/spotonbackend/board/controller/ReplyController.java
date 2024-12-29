package com.spoton.spotonbackend.board.controller;

import com.spoton.spotonbackend.board.dto.request.ReqReplyCreateDto;
import com.spoton.spotonbackend.board.dto.request.ReqReplyModifyDto;
import com.spoton.spotonbackend.board.dto.request.ReqReplyReportDto;
import com.spoton.spotonbackend.board.dto.response.ResReplyDto;
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

import java.util.List;

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

        Page<ResReplyDto> replies = replyService.list(boardId, pageable);

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
                                         @RequestParam Long boardId,
                                         @AuthenticationPrincipal TokenUserInfo userInfo) {

        replyService.delete(replyId, boardId, userInfo);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "댓글 삭제 완료", true);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    // 댓글 신고 (관리자에게 내역 메일 보내기)
    @PostMapping("/report")
    public ResponseEntity<?> sendReplyReport(@RequestBody ReqReplyReportDto dto,
                                             @AuthenticationPrincipal TokenUserInfo userInfo) {
        System.out.println(dto);
        // 신고 이메일 보내기
        String result = replyService.sendReport(dto, userInfo);

        if (result.equals("email send fail")) {
            CommonErrorDto errorDto = new CommonErrorDto(HttpStatus.SERVICE_UNAVAILABLE, "신고 실패, 다시 시도해주세요.");
            return new ResponseEntity<>(errorDto, HttpStatus.SERVICE_UNAVAILABLE);
        } else if (result.equals("existed")) {
            CommonErrorDto errorDto = new CommonErrorDto(HttpStatus.SERVICE_UNAVAILABLE, "이미 신고한 댓글입니다.");
            return new ResponseEntity<>(errorDto, HttpStatus.SERVICE_UNAVAILABLE);
        }

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "댓글이 신고되었습니다.", result);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    // 좋아요 처리
    @PostMapping("/like")
    public ResponseEntity<?> addLikeCount(@RequestParam Long replyId,
                                          @AuthenticationPrincipal TokenUserInfo userInfo) {

        // 좋아요 처리
        replyService.likeCount(replyId, userInfo);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "좋아요 처리 성공", true);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    // 댓글 좋아요 명단 조회
    @GetMapping("/like_list")
    public ResponseEntity<?> replyLikeList(@RequestParam Long replyId){
        List<String> replyLikes = replyService.replyLikeList(replyId);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "댓글 좋아요 명단 조회 완료", replyLikes);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }
}
