package com.spoton.spotonbackend.common.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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
        String requestURI = request.getRequestURI();

        if ("/user/refresh".equals(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = getTokenFromCookie(request);
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

            filterChain.doFilter(request, response);

        } catch (Exception e) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType("application/json; charset=UTF-8" );
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("토큰에 문제가 있음 (filter)");
        }
    }

    private String getTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("access_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
