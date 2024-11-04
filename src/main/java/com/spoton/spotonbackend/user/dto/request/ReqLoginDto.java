package com.spoton.spotonbackend.user.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqLoginDto {

    @NotEmpty(message = "이메일은 필수값입니다.")
    private String email;

    @NotEmpty(message = "비밀번호는 필수값입니다.")
    private String password;

    private boolean autoLogin;

    private String token;


}
