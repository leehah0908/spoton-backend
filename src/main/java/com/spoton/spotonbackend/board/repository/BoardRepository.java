package com.spoton.spotonbackend.board.repository;

import com.spoton.spotonbackend.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Map;

public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findTop10ByOrderByLikeCountDesc();
}
