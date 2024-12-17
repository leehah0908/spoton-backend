package com.spoton.spotonbackend.board.repository;

import com.spoton.spotonbackend.board.entity.BoardReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardReportRepository extends JpaRepository<BoardReport, Long> {

    boolean existsByUser_UserIdAndBoard_BoardId(Long userId, Long boardId);
}