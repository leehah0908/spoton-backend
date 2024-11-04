package com.spoton.spotonbackend.user.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import com.spoton.spotonbackend.user.entity.MyTeam;
import com.spoton.spotonbackend.user.entity.User;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqSignupDto {

    @NotEmpty(message = "이메일은 필수값입니다.")
    private String email;

    @NotEmpty(message = "비밀번호는 필수값입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상")
    private String password;

    @NotEmpty(message = "닉네임은 필수값입니다.")
    @Size(min = 2, message = "닉네임은 최소 2자 이상")
    private String nickname;

    private MultipartFile profile;

    private MyTeam myTeam;

    public User toUser(PasswordEncoder passwordEncoder) {
        return User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .build();
    }
}
