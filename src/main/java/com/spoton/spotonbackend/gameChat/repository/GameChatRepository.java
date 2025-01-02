package com.spoton.spotonbackend.gameChat.repository;

import com.spoton.spotonbackend.gameChat.entity.GameChat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GameChatRepository extends JpaRepository<GameChat, Long> {
    Optional<List<GameChat>> findByGameIdOrderByCreateTimeAsc(String gameId);
}
