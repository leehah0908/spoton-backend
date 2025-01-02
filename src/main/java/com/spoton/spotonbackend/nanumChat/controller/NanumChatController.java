package com.spoton.spotonbackend.nanumChat.controller;

import com.spoton.spotonbackend.common.auth.TokenUserInfo;
import com.spoton.spotonbackend.common.dto.CommonResDto;
import com.spoton.spotonbackend.nanumChat.dto.request.ReqChatCreateMessageDto;
import com.spoton.spotonbackend.nanumChat.dto.request.ReqChatCreateRoomDto;
import com.spoton.spotonbackend.nanumChat.dto.request.ReqChatMessageDto;
import com.spoton.spotonbackend.nanumChat.dto.response.ResNanumChatMessageDto;
import com.spoton.spotonbackend.nanumChat.dto.response.ResNanumChatRoomDto;
import com.spoton.spotonbackend.nanumChat.entity.NanumChatMessage;
import com.spoton.spotonbackend.nanumChat.entity.NanumChatRoom;
import com.spoton.spotonbackend.nanumChat.service.NanumChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/nanum_chat")
@RequiredArgsConstructor
@Slf4j
public class NanumChatController {

    private final NanumChatService nanumChatService;

    // 이전 채팅 기록 조회
    @GetMapping("/list")
    public ResponseEntity<?> getNanumChatList(@AuthenticationPrincipal TokenUserInfo userInfo) {
        List<ResNanumChatRoomDto> nanumChatList = nanumChatService.getNanumChatHistory(userInfo);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "채팅 목록 조회 성공", nanumChatList);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    // 이전 채팅 기록 조회
    @PostMapping("/message")
    public ResponseEntity<?> getNanumChatMessage(@RequestBody ReqChatMessageDto dto) {
        List<ResNanumChatMessageDto> messageList = nanumChatService.getNanumChatMessage(dto);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "메세지 조회 성공", messageList);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    // 이전 채팅 방 개설 조회
    @PostMapping("/room_create")
    public ResponseEntity<?> getNanumChatRoomCreate(@RequestBody ReqChatCreateRoomDto dto,
                                                    @AuthenticationPrincipal TokenUserInfo userInfo) {
        NanumChatRoom nanumChatRoom = nanumChatService.getNanumChatRoomCreate(dto, userInfo);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "채팅방 개설 성공", nanumChatRoom.getNanumChatRoomId());
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }


}
