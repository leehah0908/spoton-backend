package com.spoton.spotonbackend.common.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = parseBearToken(request);

        try {
            if (token != null) {
                TokenUserInfo userInfo = jwtTokenProvider.validateAndGetTokenUserInfo(token);

                List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

                // ROLE_USER or ROLE_ADMIN
                authorityList.add(new SimpleGrantedAuthority("ROLE_" + userInfo.getAuth()));

                Authentication auth = new UsernamePasswordAuthenticationToken(
                        userInfo,
                        "",
                        authorityList
                );
                SecurityContextHolder.getContext().setAuthentication(auth);

            }
//            else {
//                // 토큰 전달X or Bearer이 아님
//                response.setStatus(HttpStatus.UNAUTHORIZED.value());
//                response.setContentType("application/json");
//                response.getWriter().write("토큰이 없거나, 유효하지 않은 토큰");
//                return;
//            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType("application/json");
                response.getWriter().write("토큰에 문제가 있음");
        }
    }

    private String parseBearToken(HttpServletRequest request) {

        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
