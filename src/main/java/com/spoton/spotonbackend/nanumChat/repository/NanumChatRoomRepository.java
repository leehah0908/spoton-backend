package com.spoton.spotonbackend.nanumChat.repository;

import com.spoton.spotonbackend.nanumChat.entity.NanumChatRoom;
import com.spoton.spotonbackend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NanumChatRoomRepository extends JpaRepository<NanumChatRoom, Long> {
    List<NanumChatRoom> findByReceiverAndNanum_NanumId(User receiver, Long nanumId);

    Optional<List<NanumChatRoom>> findByProviderOrReceiver(User provider, User receiver);
}
