package com.spoton.spotonbackend.nanumChat.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spoton.spotonbackend.common.auth.TokenUserInfo;
import com.spoton.spotonbackend.nanum.entity.Nanum;
import com.spoton.spotonbackend.nanum.repository.NanumRepository;
import com.spoton.spotonbackend.nanumChat.dto.request.ReqChatCreateRoomDto;
import com.spoton.spotonbackend.nanumChat.dto.request.ReqHistoryMessageDto;
import com.spoton.spotonbackend.nanumChat.dto.request.ReqSendMessageDto;
import com.spoton.spotonbackend.nanumChat.dto.response.ResNanumChatMessageDto;
import com.spoton.spotonbackend.nanumChat.dto.response.ResNanumChatRoomDto;
import com.spoton.spotonbackend.nanumChat.entity.NanumChatMessage;
import com.spoton.spotonbackend.nanumChat.entity.NanumChatRoom;
import com.spoton.spotonbackend.nanumChat.repository.NanumChatMessageRepository;
import com.spoton.spotonbackend.nanumChat.repository.NanumChatRoomRepository;
import com.spoton.spotonbackend.user.entity.User;
import com.spoton.spotonbackend.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.spoton.spotonbackend.nanumChat.entity.QNanumChatMessage.*;
import static com.spoton.spotonbackend.nanumChat.entity.QNanumChatRoom.*;

@Service
@RequiredArgsConstructor
@Transactional
public class NanumChatService {

    private final NanumChatRoomRepository nanumChatRoomRepository;
    private final NanumChatMessageRepository nanumChatMessageRepository;

    private final UserRepository userRepository;
    private final NanumRepository nanumRepository;

    private final JPAQueryFactory queryFactory;

    public List<ResNanumChatRoomDto> getNanumChatHistory(String chatType, TokenUserInfo userInfo) {
        BooleanBuilder builder = new BooleanBuilder();

        User user = userRepository.findByEmail(userInfo.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("유저 정보를 찾을 수 없습니다.")
        );

        // 쿼리 조각 만들기
        if (chatType != null) {
            switch (chatType) {
                case "receiver" -> builder.and(nanumChatRoom.receiver.email.eq(user.getEmail()));
                case "provider" -> builder.and(nanumChatRoom.provider.email.eq(user.getEmail()));
            }
        }

        // 채팅 타입에 맞는 채팅방 조회
        List<NanumChatRoom> nanumChatRooms = queryFactory
                .selectFrom(nanumChatRoom)
                .where(builder)
                .fetch();

        return nanumChatRooms.stream().map(room -> {
            // 마지막 메세지 조회
            Tuple lastMessage = queryFactory
                    .select(nanumChatMessage.content, nanumChatMessage.createTime)
                    .from(nanumChatMessage)
                    .where(nanumChatMessage.nanumChatRoomId.nanumChatRoomId.eq(room.getNanumChatRoomId()))
                    .orderBy(nanumChatMessage.createTime.desc())
                    .limit(1)
                    .fetchOne();

            String content = lastMessage != null ? lastMessage.get(nanumChatMessage.content) : null;
            LocalDateTime lastMessageCreateTime = lastMessage != null ? lastMessage.get(nanumChatMessage.createTime) : null;

            return ResNanumChatRoomDto
                    .builder()
                    .nanumChatRoomId(room.getNanumChatRoomId())
                    .nanum(room.getNanum())
                    .nanumImage(room.getNanum().getThumbnail())
                    .provider(room.getProvider())
                    .receiver(room.getReceiver())
                    .lastMessage(content)
                    .lastMessageTime(lastMessageCreateTime)
                    .createTime(room.getCreateTime())
                    .updateTime(room.getUpdateTime())
                    .build();
        }).toList();
    }

    public List<ResNanumChatMessageDto> getNanumChatMessage(ReqHistoryMessageDto dto) {
        List<NanumChatMessage> nanumChatMessages = nanumChatMessageRepository
                .findByNanumChatRoomId_NanumChatRoomId(dto.getRoomId())
                .orElse(new ArrayList<>());

        return nanumChatMessages.stream().map(message -> ResNanumChatMessageDto
                .builder()
                .nanumChatRoomId(message.getNanumChatRoomId().getNanumChatRoomId())
                .messagId(message.getMessageId())
                .nanum(message.getNanumChatRoomId().getNanum())
                .email(message.getUser().getEmail())
                .content(message.getContent())
                .createTime(message.getCreateTime())
                .updateTime(message.getUpdateTime())
                .build()).toList();
    }

    public NanumChatRoom getNanumChatRoomCreate(ReqChatCreateRoomDto dto, TokenUserInfo userInfo) {
        User receiverUser = userRepository.findByEmail(userInfo.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("받는자 유저 정보를 찾을 수 없습니다.")
        );

        User providerrUser = userRepository.findByEmail(dto.getProviderEmail()).orElseThrow(
                () -> new EntityNotFoundException("제공자 유저 정보를 찾을 수 없습니다.")
        );


        Nanum nanum = nanumRepository.findById(dto.getNanumId()).orElseThrow(
                () -> new EntityNotFoundException("나눔 정보를 찾을 수 없습니다.")
        );

        Optional<NanumChatRoom> roomData = nanumChatRoomRepository.findByReceiverAndNanum_NanumId(receiverUser, nanum.getNanumId())
                .stream()
                .findFirst();

        return roomData.orElseGet(() -> {
            NanumChatRoom newChatRoom = new NanumChatRoom();
            newChatRoom.setProvider(providerrUser);
            newChatRoom.setReceiver(receiverUser);
            newChatRoom.setNanum(nanum);

            return nanumChatRoomRepository.save(newChatRoom);
        });
    }

    public ResNanumChatMessageDto saveMessage(ReqSendMessageDto dto) {
        User user = userRepository.findByEmail(dto.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("유저 정보를 찾을 수 없음.")
        );

        NanumChatRoom nanumChatRoom = nanumChatRoomRepository.findById(dto.getRoomId()).orElseThrow(
                () -> new EntityNotFoundException("채팅방 정보를 찾을 수 없음.")
        );

        NanumChatMessage nanumChatMessage = NanumChatMessage.builder()
                .content(dto.getContent())
                .user(user)
                .nanumChatRoomId(nanumChatRoom)
                .build();

        NanumChatMessage saveMessage = nanumChatMessageRepository.save(nanumChatMessage);

        return ResNanumChatMessageDto.builder()
                .nanumChatRoomId(saveMessage.getNanumChatRoomId().getNanumChatRoomId())
                .messagId(saveMessage.getMessageId())
                .nanum(saveMessage.getNanumChatRoomId().getNanum())
                .email(saveMessage.getUser().getEmail())
                .content(saveMessage.getContent())
                .createTime(saveMessage.getCreateTime())
                .build();
    }
}
