package com.spoton.spotonbackend.user.dto.response;

import lombok.*;
import com.spoton.spotonbackend.user.entity.Auth;
import com.spoton.spotonbackend.user.entity.MyTeam;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResDto {

    private Long userId;
    private String nickname;
    private String email;
    private String profile;
    private boolean snsLinked;
    private Auth auth;
    private MyTeam myTeam;
}
