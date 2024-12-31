package com.spoton.spotonbackend.gameChat.repository;

import com.spoton.spotonbackend.gameChat.entity.GameChat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameChatRepository extends JpaRepository<GameChat, Long> {
}
