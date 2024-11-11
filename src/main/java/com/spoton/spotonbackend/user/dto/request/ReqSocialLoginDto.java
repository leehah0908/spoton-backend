package com.spoton.spotonbackend.user.dto.request;

import com.spoton.spotonbackend.user.entity.User;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqSocialLoginDto {

    private String email;
    private String nickname;
    private String profile;

    public User toUser(PasswordEncoder passwordEncoder) {
        return User.builder()
                .email(email)
                .nickname(nickname)
                .profile(profile)
                .build();
    }
}
