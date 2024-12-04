package com.spoton.spotonbackend.board.repository;

import com.spoton.spotonbackend.board.entity.BoardLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {

    boolean existsByUserIdAndBoardId(Long userId, Long boardId);

    Optional<BoardLike> findByUserIdAndBoardId(Long userId, Long boardId);
}
