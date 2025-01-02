package com.spoton.spotonbackend.nanumChat.repository;

import com.spoton.spotonbackend.nanumChat.entity.NanumChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NanumChatRoomRepository extends JpaRepository<NanumChatRoom, Long> {
}
