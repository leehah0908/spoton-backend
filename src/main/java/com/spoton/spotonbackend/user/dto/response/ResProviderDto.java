package com.spoton.spotonbackend.user.dto.response;

import com.spoton.spotonbackend.nanum.dto.response.ResNanumDto;
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
