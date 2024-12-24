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
    private String kbo;
    @Setter
    private String mlb;
    @Setter
    private String kleague;
    @Setter
    private String epl;
    @Setter
    private String kbl;
    @Setter
    private String nba;
    @Setter
    private String kovo;
    @Setter
    private String wkovo;
    @Setter
    private String lck;

    // User와의 OneToOne 관계 설정
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    @Setter
    private User user;

}
