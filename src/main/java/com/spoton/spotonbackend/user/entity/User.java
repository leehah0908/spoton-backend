package com.spoton.spotonbackend.user.entity;

import com.spoton.spotonbackend.user.dto.response.UserResDto;
import jakarta.persistence.*;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(unique = true)
    @Setter
    private String profile;

    @Column(nullable = false)
    @Builder.Default
    private boolean snsLinked = false;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Auth auth = Auth.USER;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private MyTeam myTeam;

    public void setMyTeam(MyTeam myTeam) {
        this.myTeam = myTeam;
        myTeam.setUser(this); // 양방향 관계 설정
    }

    public UserResDto toUserResDto() {
        return UserResDto.builder()
                .userId(userId)
                .nickname(nickname)
                .email(email)
                .profile(profile)
                .snsLinked(snsLinked)
                .auth(auth)
                .myTeam(myTeam)
                .build();
    }
}
