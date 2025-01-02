package com.spoton.spotonbackend.gameChat.controller;

import com.spoton.spotonbackend.common.dto.CommonResDto;
import com.spoton.spotonbackend.gameChat.dto.request.ReqGameChatDto;
import com.spoton.spotonbackend.gameChat.dto.response.ResGameChatDto;
import com.spoton.spotonbackend.gameChat.service.GameChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class GameChatController {

    private final GameChatService gameChatService;
    private final SimpMessageSendingOperations messagingTemplate;

    // prefix를 /pub 설정 -> 클라이언트가 /pub/game_chat/{gameId}로 메시지 전송
    @MessageMapping("/game_chat/{gameId}")
    public ResponseEntity<?> sendGameChatMessage(@DestinationVariable String gameId,
                                                 @RequestBody ReqGameChatDto dto) {
        ResGameChatDto chatDto = gameChatService.saveMessage(gameId, dto);

        // 메시지 전송 (같은 게임 정보를 보고 있는 유저한테)
        messagingTemplate.convertAndSend("/sub/game_chat/" + gameId, chatDto);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "채팅 저장 성공", gameId);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    // 이전 채팅 기록 조회
    @GetMapping("/game_chat/history/{gameId}")
    public ResponseEntity<?> getGameChatHistory(@PathVariable String gameId) {
        List<ResGameChatDto> gameChatHistory = gameChatService.getGameChatHistory(gameId);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "채팅 기록 조회 성공", gameChatHistory);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }
}
