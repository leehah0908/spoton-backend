package com.spoton.spotonbackend.common.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spoton.spotonbackend.common.entity.CustomOAuth2User;
import com.spoton.spotonbackend.user.entity.User;
import com.spoton.spotonbackend.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User oAuthUser = (CustomOAuth2User) authentication.getPrincipal();
        String userEmail = oAuthUser.getName();

        User user = userRepository.findByEmail(userEmail).orElseThrow(
                () -> new EntityNotFoundException("회원정보 찾을 수 없음")
        );

        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getAuth().toString());

        // map 형태로 토큰 반환
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("token", accessToken);
        resMap.put("user_id", user.getUserId());

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(resMap));
    }
}
