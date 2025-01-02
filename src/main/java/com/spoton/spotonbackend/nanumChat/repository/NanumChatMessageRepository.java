package com.spoton.spotonbackend.nanumChat.repository;

import com.spoton.spotonbackend.nanumChat.entity.NanumChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NanumChatMessageRepository extends JpaRepository<NanumChatMessage, Long> {
    Optional<List<NanumChatMessage>> findByNanumChatRoomId_NanumChatRoomId(Long nanumChatRoomId);
}
