package com.spoton.spotonbackend.user.service;

import com.spoton.spotonbackend.common.entity.CustomOAuth2User;
import com.spoton.spotonbackend.user.dto.request.ReqSocialLoginDto;
import com.spoton.spotonbackend.user.entity.LoginType;
import com.spoton.spotonbackend.user.entity.MyTeam;
import com.spoton.spotonbackend.user.entity.User;
import com.spoton.spotonbackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
        LoginType loginType = null;

        if (clientName.equals("kakao")) {
            Map<String, String> properties = (Map<String, String>) oAuth2User.getAttributes().get("properties");
            Map<String, String> kakaoAccount = (Map<String, String>) oAuth2User.getAttributes().get("kakao_account");

            email = kakaoAccount.get("email");
            loginType = LoginType.KAKAO;
            boolean isExist = userRepository.findByEmail(email).isPresent();

            if (!isExist) {
                socialSignup(properties.get("nickname"),
                        properties.get("profile_image"),
                        email,
                        loginType);
            }

        } else if (clientName.equals("naver")) {
            Map<String, String> resultMap = (Map<String, String>) oAuth2User.getAttributes().get("response");

            email = resultMap.get("email");
            loginType = LoginType.NAVER;
            boolean isExist = userRepository.findByEmail(email).isPresent();
            System.out.println(resultMap);
            System.out.println(oAuth2User);
            System.out.println(userRequest.getClientRegistration());

            if (!isExist) {
                socialSignup(resultMap.get("nickname"),
                        resultMap.get("profile_image"),
                        email,
                        loginType);
            }
        } else if (clientName.equals("Google")) {
            Map<String, Object> resultMap = oAuth2User.getAttributes();

            email = (String) resultMap.get("email");
            loginType = LoginType.GOOGLE;
            boolean isExist = userRepository.findByEmail(email).isPresent();

            if (!isExist) {
                socialSignup((String) resultMap.get("name"),
                        (String) resultMap.get("picture"),
                        email,
                        loginType);
            }
        }
        return new CustomOAuth2User(email, loginType);
    }

    // 소셜 로그인 회원가입 (기존 가입 로그가 없을 때만)
    public void socialSignup(String nickname, String profile, String email, LoginType loginType) {
        ReqSocialLoginDto newDto = ReqSocialLoginDto.builder()
                .nickname(nickname)
                .profile(profile)
                .email(email)
                .build();

        User user = newDto.toUser(passwordEncoder);
        user.setPassword(UUID.randomUUID().toString());
        user.setLoginType(loginType);
        user.setMyTeam(new MyTeam());

        userRepository.save(user);
    }
}

