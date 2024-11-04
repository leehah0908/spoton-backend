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

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private int expiration;

    public String createToken(String email, String auth) {

        Claims claims = Jwts.claims().setSubject(email);

        claims.put("auth", auth);
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expiration * 60 * 100L * 100L)) // 30분
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public TokenUserInfo validateAndGetTokenUserInfo(String token) throws Exception {

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return TokenUserInfo.builder()
                .email(claims.getSubject())
                .auth(Auth.valueOf(claims.get("auth", String.class)))
                .build();
    }
}
