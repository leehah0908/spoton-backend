package com.spoton.spotonbackend.board.controller;

import com.spoton.spotonbackend.board.dto.request.ReqBoardCreateDto;
import com.spoton.spotonbackend.board.dto.request.ReqBoardModifyDto;
import com.spoton.spotonbackend.board.dto.request.ReqBoardSearchDto;
import com.spoton.spotonbackend.board.dto.request.ReqReportDto;
import com.spoton.spotonbackend.board.dto.response.ResBoardDto;
import com.spoton.spotonbackend.board.entity.Board;
import com.spoton.spotonbackend.board.entity.BoardLike;
import com.spoton.spotonbackend.board.service.BoardService;
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
@RequestMapping("/board")
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    private final EmailProvider emailProvider;
    private final BoardService boardService;

    // 게시물 목록 조회
    @GetMapping("/list")
    public ResponseEntity<?> boardList(@RequestBody ReqBoardSearchDto dto, Pageable pageable){

        Page<ResBoardDto> boards = boardService.list(dto, pageable);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "게시글 조회 완료", boards);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    // 게시물 작성
    @PostMapping("/create")
    public ResponseEntity<?> boardCreate(@RequestBody ReqBoardCreateDto dto,
                                         @AuthenticationPrincipal TokenUserInfo userInfo){

        Board board = boardService.create(dto, userInfo);

        CommonResDto resDto = new CommonResDto(HttpStatus.CREATED, "게시글 등록 완료", board.getBoardId());
        return new ResponseEntity<>(resDto, HttpStatus.CREATED);
    }

    // 게시물 수정
    @PatchMapping("/modify")
    public ResponseEntity<?> boardModify(@RequestBody ReqBoardModifyDto dto,
                                         @AuthenticationPrincipal TokenUserInfo userInfo){

        Board board = boardService.modify(dto, userInfo);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "게시글 수정 완료", board.getBoardId());
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    // 게시물 삭제
    @PostMapping("/delete")
    public ResponseEntity<?> boardDelete(@RequestBody ReqBoardModifyDto dto,
                                         @AuthenticationPrincipal TokenUserInfo userInfo){

        boardService.delete(dto, userInfo);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "게시글 삭제 완료", true);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    // 게시물 상세 조회
    @GetMapping("/detail")
    public ResponseEntity<?> boardDetail(@RequestParam Long boardId){

        ResBoardDto board = boardService.boardDetail(boardId);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "게시글 상세 조회 완료", board);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    // 게시물 신고 (관리자에게 내역 메일 보내기)
    @PostMapping("/report")
    public ResponseEntity<?> sendReport(@RequestBody ReqReportDto dto,
                                        @AuthenticationPrincipal TokenUserInfo userInfo){

        // 관리자에게 신고 내역 보내기
        String result = emailProvider.sendReportMail(dto, userInfo);
        if (result.equals("fail")) {
            CommonErrorDto errorDto = new CommonErrorDto(HttpStatus.SERVICE_UNAVAILABLE, "신고 실패, 다시 확인해주세요.");
            return new ResponseEntity<>(errorDto, HttpStatus.SERVICE_UNAVAILABLE);
        }

        // 신고 카운트 증가
        Board board = boardService.increaseReportCount(dto.getBoardId());

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "게시물 신고 성공", board.getBoardId());
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    // 조회수 증가
    @PostMapping("/view")
    public ResponseEntity<?> increaseViewCount(@RequestParam Long boardId){

        // 조회수 카운트 증가
        Board board = boardService.increaseViewCount(boardId);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "조회수 증가 성공", board.getBoardId());
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    // 좋아요 추가
    @PostMapping("/like_add")
    public ResponseEntity<?> addLikeCount(@RequestParam Long boardId,
                                          @AuthenticationPrincipal TokenUserInfo userInfo){

        // 좋아요 증가
        BoardLike boardLike = boardService.addLikeCount(boardId, userInfo);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "좋아요 추가 성공", boardLike);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    // 좋아요 삭제
    @PostMapping("/like_cancel")
    public ResponseEntity<?> cancelLikeCount(@RequestParam Long boardId,
                                             @AuthenticationPrincipal TokenUserInfo userInfo){

        // 좋아요 삭제
        boardService.cancelLikeCount(boardId, userInfo);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "좋아요 삭제 성공", true);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }
}
