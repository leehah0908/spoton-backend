package com.spoton.spotonbackend.board.repository;

import com.spoton.spotonbackend.board.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
}
