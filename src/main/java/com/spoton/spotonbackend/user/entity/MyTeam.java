package com.spoton.spotonbackend.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "my_team")
public class MyTeam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mtId;

    private String kboTeam;
    private String mlbTeam;
    private String kleagueTeam;
    private String eplTeam;
    private String kblTeam;
    private String nbaTeam;
    private String kovoTeam;
    private String wkovwTeam;
    private String lckTeam;

    // User와의 OneToOne 관계 설정
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @Setter
    private User user;
}
