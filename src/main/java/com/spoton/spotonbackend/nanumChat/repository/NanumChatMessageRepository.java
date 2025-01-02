package com.spoton.spotonbackend.nanumChat.repository;

import com.spoton.spotonbackend.nanumChat.entity.NanumChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NanumChatMessageRepository extends JpaRepository<NanumChatMessage, Long> {
}
