package com.spoton.spotonbackend.nanum.controller;

import com.spoton.spotonbackend.board.dto.request.ReqBoardReportDto;
import com.spoton.spotonbackend.board.entity.Board;
import com.spoton.spotonbackend.common.auth.TokenUserInfo;
import com.spoton.spotonbackend.common.dto.CommonErrorDto;
import com.spoton.spotonbackend.common.dto.CommonResDto;
import com.spoton.spotonbackend.nanum.dto.request.ReqNanumCreateDto;
import com.spoton.spotonbackend.nanum.dto.request.ReqNanumModifyDto;
import com.spoton.spotonbackend.nanum.dto.request.ReqNanumReportDto;
import com.spoton.spotonbackend.nanum.dto.response.ResNanumDto;
import com.spoton.spotonbackend.nanum.entity.Nanum;
import com.spoton.spotonbackend.nanum.service.NanumService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/nanum")
@RequiredArgsConstructor
@Slf4j
public class NanumController {

    private final NanumService nanumService;

    // 게시물 목록 조회
    @GetMapping("/list")
    public ResponseEntity<?> nanumList(@RequestParam String searchType,
                                       @RequestParam String searchKeyword,
                                       Pageable pageable){
        Page<ResNanumDto> nanumList = nanumService.list(searchType, searchKeyword, pageable);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "나눔 글 조회 완료", nanumList);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    // 나눔글 작성
    @PostMapping("/create")
    public ResponseEntity<?> nanumCreate(@RequestPart(value = "dto") ReqNanumCreateDto dto,
                                         @RequestPart(value = "imagePaths") List<MultipartFile> imagePaths,
                                         @AuthenticationPrincipal TokenUserInfo userInfo){
        Nanum nanum = nanumService.create(dto, imagePaths, userInfo);

        CommonResDto resDto = new CommonResDto(HttpStatus.CREATED, "나눔글 등록 완료", nanum.getNanumId());
        return new ResponseEntity<>(resDto, HttpStatus.CREATED);
    }

    // 나눔글 수정
    @PatchMapping("/modify")
    public ResponseEntity<?> nanumModify(@RequestPart(value = "dto") ReqNanumModifyDto dto,
                                         @RequestPart(value = "imagePaths") List<MultipartFile> imagePaths,
                                         @AuthenticationPrincipal TokenUserInfo userInfo){
        Nanum nanum = nanumService.modify(dto, imagePaths, userInfo);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "나눔글 수정 완료", nanum.getNanumId());
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    // 나눔글 삭제
    @PostMapping("/delete")
    public ResponseEntity<?> nanumDelete(@RequestParam Long nanumId,
                                         @AuthenticationPrincipal TokenUserInfo userInfo){
        nanumService.delete(nanumId, userInfo);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "나눔글 삭제 완료", true);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    // 나눔글 상세 조회
    @GetMapping("/detail")
    public ResponseEntity<?> nanumDetail(@RequestParam Long nanumId){
        ResNanumDto nanum = nanumService.nanumDetail(nanumId);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "나눔글 상세 조회 완료", nanum);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    // 게시물 신고 (관리자에게 내역 메일 보내기)
    @PostMapping("/report")
    public ResponseEntity<?> sendNanumReport(@RequestBody ReqNanumReportDto dto,
                                             @AuthenticationPrincipal TokenUserInfo userInfo){
        // 신고 이메일 보내기
        String result = nanumService.sendReport(dto, userInfo);

        if (result.equals("email send fail")) {
            CommonErrorDto errorDto = new CommonErrorDto(HttpStatus.SERVICE_UNAVAILABLE, "신고 실패, 다시 시도해주세요.");
            return new ResponseEntity<>(errorDto, HttpStatus.SERVICE_UNAVAILABLE);
        } else if (result.equals("existed")) {
            CommonErrorDto errorDto = new CommonErrorDto(HttpStatus.SERVICE_UNAVAILABLE, "이미 신고한 나눔글입니다.");
            return new ResponseEntity<>(errorDto, HttpStatus.SERVICE_UNAVAILABLE);
        }

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "나눔글이 신고되었습니다.", result);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    // 조회수 증가
    @PostMapping("/view")
    public ResponseEntity<?> increaseViewCount(@RequestParam Long nanumId){
        // 조회수 카운트 증가
        Nanum nanum = nanumService.increaseViewCount(nanumId);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "조회수 증가 성공", nanum.getNanumId());
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    // 좋아요 추가 및 삭제
    @PostMapping("/like")
    public ResponseEntity<?> addLikeCount(@RequestParam Long nanumId,
                                          @AuthenticationPrincipal TokenUserInfo userInfo){
        // 좋아요 처리
        nanumService.likeCount(nanumId, userInfo);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "좋아요 처리 성공", true);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    // 나눔글 좋아요 명단 조회
    @GetMapping("/like_list")
    public ResponseEntity<?> nanumLikeList(@RequestParam Long nanumId){
        List<String> nanumLikes = nanumService.nanumLikeList(nanumId);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "나눔 찜 명단 조회 완료", nanumLikes);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    // 최신 나눔글 조회
    @GetMapping("/lastest_nanum")
    public ResponseEntity<?> lastestNanum(){
        List<ResNanumDto> lastestNanumList = nanumService.lastestNanum();

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "최신 나눔글 조회 완료", lastestNanumList);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }
}
