package com.spoton.spotonbackend.board.controller;

import com.spoton.spotonbackend.board.dto.request.ReqBoardCreateDto;
import com.spoton.spotonbackend.board.dto.request.ReqBoardModifyDto;
import com.spoton.spotonbackend.board.dto.request.ReqBoardReportDto;
import com.spoton.spotonbackend.board.dto.response.ResBoardDto;
import com.spoton.spotonbackend.board.entity.Board;
import com.spoton.spotonbackend.board.service.BoardService;
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
@RequestMapping("/board")
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    private final BoardService boardService;

    // 게시물 목록 조회
    @GetMapping("/list")
    public ResponseEntity<?> boardList(@RequestParam String searchType,
                                       @RequestParam String searchKeyword,
                                       Pageable pageable){
        Page<ResBoardDto> boards = boardService.list(searchType, searchKeyword, pageable);

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
    public ResponseEntity<?> boardDelete(@RequestParam Long boardId,
                                         @AuthenticationPrincipal TokenUserInfo userInfo){
        boardService.delete(boardId, userInfo);

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
    public ResponseEntity<?> sendBoardReport(@RequestBody ReqBoardReportDto dto,
                                             @AuthenticationPrincipal TokenUserInfo userInfo){
        // 신고 이메일 보내기
        String result = boardService.sendReport(dto, userInfo);

        if (result.equals("email send fail")) {
            CommonErrorDto errorDto = new CommonErrorDto(HttpStatus.SERVICE_UNAVAILABLE, "신고 실패, 다시 시도해주세요.");
            return new ResponseEntity<>(errorDto, HttpStatus.SERVICE_UNAVAILABLE);
        } else if (result.equals("existed")) {
            CommonErrorDto errorDto = new CommonErrorDto(HttpStatus.SERVICE_UNAVAILABLE, "이미 신고한 게시물입니다.");
            return new ResponseEntity<>(errorDto, HttpStatus.SERVICE_UNAVAILABLE);
        }

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "게시물이 신고되었습니다.", result);
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

    // 좋아요 추가 및 삭제
    @PostMapping("/like")
    public ResponseEntity<?> addLikeCount(@RequestParam Long boardId,
                                          @AuthenticationPrincipal TokenUserInfo userInfo){
        // 좋아요 처리
        boardService.likeCount(boardId, userInfo);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "좋아요 처리 성공", true);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    @GetMapping("/hot_board")
    public ResponseEntity<?> hotBoard(){
        List<ResBoardDto> hotBoardList = boardService.hotBoard();

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "베스트 게시물 조회 완료", hotBoardList);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }
}
