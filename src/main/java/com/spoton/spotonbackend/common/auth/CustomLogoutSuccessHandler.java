package com.spoton.spotonbackend.common.auth;

import com.spoton.spotonbackend.user.entity.LoginType;
import com.spoton.spotonbackend.user.entity.User;
import com.spoton.spotonbackend.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoAppKey;

    @Override
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {

        String redirectUri = "https://onspoton.com";
        LoginType loginType = null;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("access_token".equals(cookie.getName())) {
                    try {
                        TokenUserInfo tokenUserInfo = jwtTokenProvider.validateAndGetTokenUserInfo(cookie.getValue());
                        User user = userRepository.findByEmail(tokenUserInfo.getEmail()).orElseThrow(
                                () -> new EntityNotFoundException("회원 정보를 찾을 수 없습니다.")
                        );

                        loginType = user.getLoginType();
                        redisTemplate.delete(user.getEmail());

                        cookie.setValue("");
                        cookie.setPath("/");
                        cookie.setMaxAge(0);
                        cookie.setSecure(true);
                        cookie.setHttpOnly(true);
                        cookie.setAttribute("SameSite", "None");
                        response.addCookie(cookie);

                    } catch (Exception e) {
                        response.setStatus(HttpStatus.UNAUTHORIZED.value());
                        response.setContentType("application/json");
                        response.getWriter().write("토큰에 문제가 있음 (filter)");
                    }
                }
            }
        }

        if (loginType != LoginType.COMMON) {
            if (loginType == LoginType.KAKAO) {
                String kakaoLogoutUrl = "https://kauth.kakao.com/oauth/logout?client_id=" +
                        kakaoAppKey +
                        "&logout_redirect_uri=" +
                        redirectUri;

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(kakaoLogoutUrl);
            } else if (loginType == LoginType.NAVER) {
                String naverLogoutUrl = "NAVER API does not provide a logout API.";

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(naverLogoutUrl);
            } else if (loginType == LoginType.GOOGLE) {
                String googleLogoutUrl = "GOOGLE API does not provide a logout API.";

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(googleLogoutUrl);
            }
        } else {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(redirectUri);
        }
    }
}
