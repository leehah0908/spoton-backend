package com.spoton.spotonbackend.board.repository;

import com.spoton.spotonbackend.board.entity.ReplyLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReplyLikeRepository extends JpaRepository<ReplyLike, Long> {

    boolean existsByUser_UserIdAndReply_ReplyId(Long userId, Long replyId);

    Optional<ReplyLike> findByUser_UserIdAndReply_ReplyId(Long userId, Long replyId);
}
