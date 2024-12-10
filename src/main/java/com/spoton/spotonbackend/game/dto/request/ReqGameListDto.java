package com.spoton.spotonbackend.game.dto.request;

import com.spoton.spotonbackend.user.entity.MyTeam;
import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqGameListDto {

    private int year;
    private int month;
    private String sports;
    private String league;
    private String team;

}
