package com.spoton.spotonbackend.common.auth;

import com.spoton.spotonbackend.user.entity.Auth;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenUserInfo {
    private String email;
    private Auth auth;
}
