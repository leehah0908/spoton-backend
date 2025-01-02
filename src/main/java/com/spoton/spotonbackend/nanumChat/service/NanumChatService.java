package com.spoton.spotonbackend.nanumChat.service;

import com.spoton.spotonbackend.common.auth.TokenUserInfo;
import com.spoton.spotonbackend.nanum.entity.Nanum;
import com.spoton.spotonbackend.nanum.repository.NanumRepository;
import com.spoton.spotonbackend.nanumChat.dto.request.ReqChatCreateRoomDto;
import com.spoton.spotonbackend.nanumChat.dto.request.ReqChatMessageDto;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class NanumChatService {

    private final NanumChatRoomRepository nanumChatRoomRepository;
    private final NanumChatMessageRepository nanumChatMessageRepository;

    private final UserRepository userRepository;
    private final NanumRepository nanumRepository;

    public List<ResNanumChatRoomDto> getNanumChatHistory(TokenUserInfo userInfo) {
        User user = userRepository.findByEmail(userInfo.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("유저 정보를 찾을 수 없습니다.")
        );

        List<NanumChatRoom> nanumChatRooms = nanumChatRoomRepository
                .findByProviderOrReceiver(user, user)
                .orElse(new ArrayList<>());

        return nanumChatRooms.stream().map(room -> ResNanumChatRoomDto
                .builder()
                .nanumChatRoomId(room.getNanumChatRoomId())
                .nanumId(room.getNanum().getNanumId())
                .provider(room.getProvider())
                .receiver(room.getReceiver())
                .createTime(room.getCreateTime())
                .updateTime(room.getUpdateTime())
                .build()).toList();
    }

    public List<ResNanumChatMessageDto> getNanumChatMessage(ReqChatMessageDto dto) {

        List<NanumChatMessage> nanumChatMessages = nanumChatMessageRepository
                .findByNanumChatRoomId_NanumChatRoomId(dto.getRoomId())
                .orElse(new ArrayList<>());

        return nanumChatMessages.stream().map(message -> ResNanumChatMessageDto
                .builder()
                .nanumChatRoomId(message.getNanumChatRoomId().getNanumChatRoomId())
                .messagId(message.getMessageId())
                .nanumId(message.getNanumChatRoomId().getNanum().getNanumId())
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
}
