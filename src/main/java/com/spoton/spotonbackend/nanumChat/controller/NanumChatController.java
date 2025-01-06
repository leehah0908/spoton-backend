package com.spoton.spotonbackend.nanumChat.controller;

import com.spoton.spotonbackend.common.auth.TokenUserInfo;
import com.spoton.spotonbackend.common.dto.CommonResDto;
import com.spoton.spotonbackend.nanumChat.dto.request.ReqChatCreateRoomDto;
import com.spoton.spotonbackend.nanumChat.dto.request.ReqHistoryMessageDto;
import com.spoton.spotonbackend.nanumChat.dto.request.ReqSendMessageDto;
import com.spoton.spotonbackend.nanumChat.dto.response.ResNanumChatMessageDto;
import com.spoton.spotonbackend.nanumChat.dto.response.ResNanumChatRoomDto;
import com.spoton.spotonbackend.nanumChat.entity.NanumChatRoom;
import com.spoton.spotonbackend.nanumChat.service.NanumChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class NanumChatController {

    private final NanumChatService nanumChatService;
    private final SimpMessageSendingOperations messagingTemplate;

    // 이전 채팅 목록 조회
    @GetMapping("/chat/nanum_chat/list")
    public ResponseEntity<?> getNanumChatList(@RequestParam String chatType,
                                              @AuthenticationPrincipal TokenUserInfo userInfo) {
        List<ResNanumChatRoomDto> nanumChatList = nanumChatService.getNanumChatHistory(chatType, userInfo);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "채팅 목록 조회 성공", nanumChatList);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    // 이전 채팅 메세지 조회
    @PostMapping("/chat/nanum_chat/message")
    public ResponseEntity<?> getNanumChatMessage(@RequestBody ReqHistoryMessageDto dto) {
        List<ResNanumChatMessageDto> messageList = nanumChatService.getNanumChatMessage(dto);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "메세지 조회 성공", messageList);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    // 채팅 방 개설 및 조회
    @PostMapping("/chat/nanum_chat/room_create")
    public ResponseEntity<?> getNanumChatRoomCreate(@RequestBody ReqChatCreateRoomDto dto,
                                                    @AuthenticationPrincipal TokenUserInfo userInfo) {
        NanumChatRoom nanumChatRoom = nanumChatService.getNanumChatRoomCreate(dto, userInfo);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "채팅방 개설 성공", nanumChatRoom.getNanumChatRoomId());
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    // 전역 메시지 발행
    @MessageMapping("/chat/nanum_chat/global")
    public void nanumChatGlobal(ReqSendMessageDto dto) {
        ResNanumChatMessageDto chatDto = nanumChatService.saveMessage(dto);

        // 전역 구독 메시지 전송
        // for 채팅 리스트 최신화
        messagingTemplate.convertAndSend("/sub/chat/nanum_chat/global" , chatDto);
        // for 채팅방 메세지 최신화
        messagingTemplate.convertAndSend("/sub/chat/nanum_chat/" + dto.getRoomId() , chatDto);
    }
}
