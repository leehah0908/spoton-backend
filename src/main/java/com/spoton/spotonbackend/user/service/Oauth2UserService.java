package com.spoton.spotonbackend.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spoton.spotonbackend.common.dto.CommonErrorDto;
import com.spoton.spotonbackend.user.dto.request.ReqSocialLoginDto;
import com.spoton.spotonbackend.user.entity.MyTeam;
import com.spoton.spotonbackend.user.entity.User;
import com.spoton.spotonbackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
public class Oauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        String clientName = userRequest.getClientRegistration().getClientName();

        if (clientName.equals("kakao")) {
            Map<String, String> properties = (Map<String, String>) oAuth2User.getAttributes().get("properties");
            Map<String, String> kakaoAccount = (Map<String, String>) oAuth2User.getAttributes().get("kakao_account");

            boolean isExist = userRepository.findByEmail(kakaoAccount.get("email")).isPresent();

            if (!isExist) {
                System.out.println("회원가입");
                ReqSocialLoginDto newDto = ReqSocialLoginDto.builder()
                        .nickname(properties.get("nickname"))
                        .profile(properties.get("profile_image"))
                        .email(kakaoAccount.get("email"))
                        .build();

                User user = newDto.toUser(passwordEncoder);
                user.setPassword(UUID.randomUUID().toString());
                user.setSnsLinked(true);
                user.setMyTeam(new MyTeam());

                userRepository.save(user);
            }
            System.out.println("회원가입 안함");
        } else if (clientName.equals("naver")) {

        }


        return oAuth2User;
    }
}
