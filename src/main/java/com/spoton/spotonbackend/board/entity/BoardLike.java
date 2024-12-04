package com.spoton.spotonbackend.board.entity;

import com.spoton.spotonbackend.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "board_like")
public class BoardLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardLikeId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @Setter
    private User user;

    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    @Setter
    private Board board;

}
