package com.spoton.spotonbackend.common.auth;

import com.spoton.spotonbackend.common.entity.CustomOAuth2User;
import com.spoton.spotonbackend.user.entity.User;
import com.spoton.spotonbackend.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User oAuthUser = (CustomOAuth2User) authentication.getPrincipal();
        String userEmail = oAuthUser.getName();

        User user = userRepository.findByEmail(userEmail).orElseThrow(
                () -> new EntityNotFoundException("회원정보 찾을 수 없음")
        );

        user.setLoginType(oAuthUser.getLoginType());

        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getAuth().toString());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail(), user.getAuth().toString());

        // 리프레시 토큰 redis에 저장
        redisTemplate.opsForValue().set(String.valueOf(user.getEmail()), refreshToken, 14400, TimeUnit.MINUTES);

        // 쿠키로 로그인 인증
        Cookie cookie = new Cookie("access_token", accessToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(10 * 24 * 60 * 60); // 쿠키 유효기간 10일
        cookie.setAttribute("SameSite", "None");

        response.addCookie(cookie);
        response.sendRedirect("http://localhost:3000");
    }
}
