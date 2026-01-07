package com.teamB.hospitalreservation.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/*
JWT(JSON Web Token)를 생성하고 검증하는 유틸리티 클래스입니다.
generateToken() : 사용자의 아이디 (username)를 받아 암호화된 JWT를 생성합니다. 토큰에는 유효 기간이 포함됩니다.

getUsernameFromToken(): 클라이언트로부터 받은 JWT를 복호화하여 사용자의 아이디를 추출합니다.
                        토큰이 유효한지 (변조되지 않았는지, 만료되지 않았는지) 검증하는 역할을 합니다.
 */

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration_ms}")
    private Long expirationMs;

    private Key getSigningKey() {
        return new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(SignatureAlgorithm.HS256, getSigningKey())
                .compact();
    }

}