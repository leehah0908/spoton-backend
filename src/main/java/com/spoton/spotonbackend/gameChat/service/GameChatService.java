package com.spoton.spotonbackend.gameChat.service;

import com.spoton.spotonbackend.gameChat.dto.request.ReqGameChatDto;
import com.spoton.spotonbackend.gameChat.dto.response.ResGameChatDto;
import com.spoton.spotonbackend.gameChat.entity.GameChat;
import com.spoton.spotonbackend.gameChat.repository.GameChatRepository;
import com.spoton.spotonbackend.user.entity.User;
import com.spoton.spotonbackend.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GameChatService {

    private final GameChatRepository gameChatRepository;
    private final UserRepository userRepository;

    public ResGameChatDto saveMessage(String gameId, ReqGameChatDto dto) {
        User user = userRepository.findByEmail(dto.getSenderEmail()).orElseThrow(
                () -> new EntityNotFoundException("유저 정보를 찾을 수 없음.")
        );

        GameChat gameChat = GameChat.builder()
                .gameId(gameId)
                .user(user)
                .content(dto.getContent())
                .build();

        GameChat saveMessage = gameChatRepository.save(gameChat);

        return ResGameChatDto.builder()
                .gameId(saveMessage.getGameId())
                .email(saveMessage.getUser().getEmail())
                .nickname(saveMessage.getUser().getNickname())
                .content(saveMessage.getContent())
                .createTime(saveMessage.getCreateTime())
                .build();
    }

    public List<ResGameChatDto> getGameChatHistory(String gameId) {
        List<GameChat> gameChats = gameChatRepository.findByGameIdOrderByCreateTimeAsc(gameId).orElse(new ArrayList<>());

        return gameChats.stream().map(chat -> ResGameChatDto.builder()
                        .gameId(chat.getGameId())
                        .email(chat.getUser().getEmail())
                        .nickname(chat.getUser().getNickname())
                        .content(chat.getContent())
                        .createTime(chat.getCreateTime())
                        .build())
                .toList();
    }
}
