package com.spoton.spotonbackend.board.repository;

import com.spoton.spotonbackend.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findTop10ByOrderByLikeCountDesc();

    Optional<List<Board>> findByUser_UserIdOrderByCreateTimeDesc(Long userId);

    @Query("SELECT bl.board FROM BoardLike bl WHERE bl.user.userId = :userId ORDER BY bl.board.createTime DESC")
    Optional<List<Board>> findBoardsLikedByUser(@Param("userId") Long userId);
}
