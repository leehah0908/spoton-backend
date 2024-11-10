package com.spoton.spotonbackend.user.entity;

import com.spoton.spotonbackend.user.dto.response.UserResDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

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
    @Setter
    private String password;

    @Column(unique = true, nullable = false)
    @Setter
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

    @OneToOne(mappedBy = "user", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private MyTeam myTeam;

    public void setMyTeam(MyTeam myTeam) {
        // myTeam이 null일 경우, 모든 필드가 null인 빈 MyTeam 객체 생성
        if (myTeam == null) {
            myTeam = new MyTeam();
        }

        this.myTeam = myTeam;
        myTeam.setUser(this); // 항상 양방향 연관 관계 설정
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
