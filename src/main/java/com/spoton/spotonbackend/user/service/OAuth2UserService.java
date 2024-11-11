package com.spoton.spotonbackend.user.service;

import com.spoton.spotonbackend.common.entity.CustomOAuth2User;
import com.spoton.spotonbackend.user.dto.request.ReqSocialLoginDto;
import com.spoton.spotonbackend.user.entity.MyTeam;
import com.spoton.spotonbackend.user.entity.User;
import com.spoton.spotonbackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.NativeQuery;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        String clientName = userRequest.getClientRegistration().getClientName();
        String email = null;

        if (clientName.equals("kakao")) {
            Map<String, String> properties = (Map<String, String>) oAuth2User.getAttributes().get("properties");
            Map<String, String> kakaoAccount = (Map<String, String>) oAuth2User.getAttributes().get("kakao_account");

            email = kakaoAccount.get("email");
            boolean isExist = userRepository.findByEmail(email).isPresent();

            if (!isExist) {
                socialSignup(properties.get("nickname"),
                        properties.get("profile_image"),
                        kakaoAccount.get("email"));
            }

        } else if (clientName.equals("naver")) {
            Map<String, String> resultMap = (Map<String, String>) oAuth2User.getAttributes().get("response");
            System.out.println(resultMap);

            email = resultMap.get("email");
            boolean isExist = userRepository.findByEmail(email).isPresent();

            if (!isExist) {
                socialSignup(resultMap.get("nickname"),
                        resultMap.get("profile_image"),
                        resultMap.get("email"));
            }
        }
        return new CustomOAuth2User(email);
    }

    // 소셜 로그인 회원가입
    public void socialSignup(String nickname, String profile, String email) {
        ReqSocialLoginDto newDto = ReqSocialLoginDto.builder()
                .nickname(nickname)
                .profile(profile)
                .email(email)
                .build();

        User user = newDto.toUser(passwordEncoder);
        user.setPassword(UUID.randomUUID().toString());
        user.setSnsLinked(true);
        user.setMyTeam(new MyTeam());

        userRepository.save(user);
    }
}

