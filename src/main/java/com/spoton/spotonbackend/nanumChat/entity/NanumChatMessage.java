package com.spoton.spotonbackend.nanumChat.entity;

import com.spoton.spotonbackend.common.entity.BaseTimeEntity;
import com.spoton.spotonbackend.gameChat.entity.GameChat;
import com.spoton.spotonbackend.nanum.entity.Nanum;
import com.spoton.spotonbackend.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "nanum_chat_message")
public class NanumChatMessage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @Column(nullable = false)
    @Setter
    private String content;

    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Setter
    @ManyToOne
    @JoinColumn(name = "nanum_chat_room_id", nullable = false)
    private NanumChatRoom nanumChatRoomId;
}
