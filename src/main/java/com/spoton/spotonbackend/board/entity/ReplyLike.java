package com.spoton.spotonbackend.board.entity;

import com.spoton.spotonbackend.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "reply_like")
public class ReplyLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long replyLikeId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

}
