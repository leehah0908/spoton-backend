package com.spoton.spotonbackend.nanum.entity;

import com.spoton.spotonbackend.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "nanum_like")
public class NanumLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long nanumLikeId;

    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Setter
    @ManyToOne
    @JoinColumn(name = "nanum_id", nullable = false)
    private Nanum nanum;
}
