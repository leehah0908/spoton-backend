package com.spoton.spotonbackend.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.spoton.spotonbackend.board.entity.Board;
import com.spoton.spotonbackend.board.entity.Reply;
import com.spoton.spotonbackend.common.entity.BaseTimeEntity;
import com.spoton.spotonbackend.nanum.entity.Nanum;
import com.spoton.spotonbackend.user.dto.response.ResProviderDto;
import com.spoton.spotonbackend.user.dto.response.UserResDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "user")
public class User extends BaseTimeEntity {

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
    @Setter
    private boolean numberCertification = false;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Setter
    private LoginType loginType = LoginType.COMMON;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Auth auth = Auth.USER;

    @OneToOne(mappedBy = "user", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private MyTeam myTeam;

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JsonIgnore
    private List<Board> boards = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JsonIgnore
    private List<Reply> replies = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JsonIgnore
    private List<Nanum> nanums = new ArrayList<>();

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
                .loginType(loginType)
                .auth(auth)
                .myTeam(myTeam)
                .build();
    }

    public ResProviderDto toResProviderDto() {
        return ResProviderDto.builder()
                .userId(userId)
                .nickname(nickname)
                .email(email)
                .profile(profile)
                .build();
    }
}
