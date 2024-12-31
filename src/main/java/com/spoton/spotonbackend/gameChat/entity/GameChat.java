package com.spoton.spotonbackend.gameChat.entity;

import com.spoton.spotonbackend.common.entity.BaseTimeEntity;
import com.spoton.spotonbackend.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "game_chat")
public class GameChat extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameChatId;

    @Column(nullable = false)
    @Setter
    private String gameId;

    @Column(nullable = false)
    @Setter
    private String content;

    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
