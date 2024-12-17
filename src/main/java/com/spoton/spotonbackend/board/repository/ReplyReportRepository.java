package com.spoton.spotonbackend.board.repository;

import com.spoton.spotonbackend.board.entity.ReplyReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyReportRepository extends JpaRepository<ReplyReport, Long> {

    boolean existsByUser_UserIdAndReply_ReplyId(Long userId, Long replyId);
}