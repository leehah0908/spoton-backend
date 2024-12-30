package com.spoton.spotonbackend.user.dto.response;

import com.spoton.spotonbackend.nanum.dto.response.ResNanumDto;
import com.spoton.spotonbackend.nanum.entity.Nanum;
import com.spoton.spotonbackend.user.entity.Auth;
import com.spoton.spotonbackend.user.entity.LoginType;
import com.spoton.spotonbackend.user.entity.MyTeam;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResProviderDto {

    private Long userId;
    private String nickname;
    private String email;
    private String profile;
    private LocalDateTime createTime;

    private Long reportCount;
    private List<ResNanumDto> nanumList;
}
