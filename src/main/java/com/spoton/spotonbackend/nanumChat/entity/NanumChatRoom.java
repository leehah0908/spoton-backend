package com.spoton.spotonbackend.nanumChat.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.spoton.spotonbackend.common.entity.BaseTimeEntity;
import com.spoton.spotonbackend.nanum.entity.Nanum;
import com.spoton.spotonbackend.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "nanum_chat_room")
public class NanumChatRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long nanumChatRoomId;

    @Setter
    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    private User provider;

    @Setter
    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Setter
    @ManyToOne
    @JoinColumn(name = "nanum_id", nullable = false)
    private Nanum nanum;

    @OneToMany(mappedBy = "nanumChatRoomId", cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JsonIgnore
    private List<NanumChatMessage> nanumChatMessageList = new ArrayList<>();
}
