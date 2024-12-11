package com.spoton.spotonbackend.user.dto.request;

import com.spoton.spotonbackend.user.entity.MyTeam;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqModifyDto {

    private String type;
    private String nickname;
    private MyTeam myTeam;

}
