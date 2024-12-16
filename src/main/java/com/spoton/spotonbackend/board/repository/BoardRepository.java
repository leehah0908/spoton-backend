package com.spoton.spotonbackend.board.repository;

import com.spoton.spotonbackend.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {


}
