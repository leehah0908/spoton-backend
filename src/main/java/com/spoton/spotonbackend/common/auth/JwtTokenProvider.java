package com.spoton.spotonbackend.common.auth;

import com.spoton.spotonbackend.user.entity.Auth;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.acc.secretKey}")
    private String accSecretKey;

    @Value("${jwt.acc.expiration}")
    private int accExpiration;

    @Value("${jwt.rf.secretKey}")
    private String rfSecretKey;

    @Value("${jwt.rf.expiration}")
    private int rfExpiration;

    // 액세스 토큰 생성
    public String createAccessToken(String email, String auth) {

        Claims claims = Jwts.claims().setSubject(email);

        claims.put("auth", auth);
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accExpiration * 60 * 1000L))
                .signWith(SignatureAlgorithm.HS256, accSecretKey)
                .compact();
    }

    // 리프레시 토큰 생성
    public String createRefreshToken(String email, String auth) {

        Claims claims = Jwts.claims().setSubject(email);

        claims.put("auth", auth);
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + rfExpiration * 60 * 1000L))
                .signWith(SignatureAlgorithm.HS256, rfSecretKey)
                .compact();
    }

    // 토큰 검증
    public TokenUserInfo validateAndGetTokenUserInfo(String token) throws Exception {

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(accSecretKey)
                .build()
                .parseClaimsJws(token) // 토큰이 유효한지 검증
                .getBody();

        return TokenUserInfo.builder()
                .email(claims.getSubject())
                .auth(Auth.valueOf(claims.get("auth", String.class)))
                .build();
    }
}
