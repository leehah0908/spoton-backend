package com.spoton.spotonbackend.nanumChat.repository;

import com.spoton.spotonbackend.nanumChat.entity.NanumChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface NanumChatMessageRepository extends JpaRepository<NanumChatMessage, Long> {
    Optional<List<NanumChatMessage>> findByNanumChatRoomId_NanumChatRoomId(Long nanumChatRoomId);

    @Query("SELECT m FROM NanumChatMessage m " +
            "WHERE m.user.userId = :userId " +
            "AND m.createTime = (" +
            "  SELECT MAX(m2.createTime) " +
            "  FROM NanumChatMessage m2 " +
            "  WHERE m2.nanumChatRoomId = m.nanumChatRoomId" +
            ")")
    List<NanumChatMessage> findLatestMessagesByUserId(@Param("userId") Long userId);
}
