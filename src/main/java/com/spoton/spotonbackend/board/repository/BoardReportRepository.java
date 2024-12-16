package com.spoton.spotonbackend.board.repository;

import com.spoton.spotonbackend.board.entity.BoardReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardReportRepository extends JpaRepository<BoardReport, Long> {

    boolean existsByUser_UserIdAndBoard_BoardId(Long userId, Long boardId);

    Optional<BoardReport> findByUser_UserIdAndBoard_BoardId(Long userId, Long boardId);
}