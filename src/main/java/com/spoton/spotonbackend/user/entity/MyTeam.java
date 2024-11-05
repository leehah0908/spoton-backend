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

    @Setter
    private String kboTeam;
    @Setter
    private String mlbTeam;
    @Setter
    private String kleagueTeam;
    @Setter
    private String eplTeam;
    @Setter
    private String kblTeam;
    @Setter
    private String nbaTeam;
    @Setter
    private String kovoTeam;
    @Setter
    private String wkovwTeam;
    @Setter
    private String lckTeam;

    // User와의 OneToOne 관계 설정
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    @Setter
    private User user;

}
