package com.spoton.spotonbackend.common.configs;

import com.spoton.spotonbackend.common.auth.CustomLogoutSuccessHandler;
import com.spoton.spotonbackend.common.auth.JwtAuthFilter;
import com.spoton.spotonbackend.common.auth.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final DefaultOAuth2UserService defaultOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    auth
                            .requestMatchers(
                                    "/oauth2/**",
                                    "/user/signup",
                                    "/user/login",
                                    "/user/email_send",
                                    "/user/email_certi",
                                    "/user/pw_send",
                                    "/user/check_email",
                                    "/user/check_nickname",
                                    "/user/refresh",
                                    "/board/list",
                                    "/board/detail",
                                    "/board/view",
                                    "/board/hot_board",
                                    "/reply/list",
                                    "/game/list",
                                    "/game/detail",
                                    "/game/today",
                                    "/nanum/list",
                                    "/nanum/detail",
                                    "/nanum/lastest_nanum",
                                    "/nanum/view"
                            )
                            .permitAll()
                            .anyRequest()
                            .authenticated();

                })
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(endpoint -> endpoint.baseUri("/social_login"))
                        .redirectionEndpoint(endpoint -> endpoint.baseUri("/oauth2/callback/*"))
                        .userInfoEndpoint(endpoint -> endpoint.userService(defaultOAuth2UserService))
                        .successHandler(oAuth2SuccessHandler))
                .logout((logout) ->
                        logout.logoutSuccessHandler(customLogoutSuccessHandler))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
