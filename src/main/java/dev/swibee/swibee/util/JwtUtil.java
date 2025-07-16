package dev.swibee.swibee.util;

import dev.swibee.swibee.config.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {
    
    private final JwtProperties jwtProperties;
    
    // JWT 비밀키 생성
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }
    
    // Access Token 생성
    public String generateAccessToken(String memberId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtProperties.getAccessToken().getExpiration());
        
        return Jwts.builder()
                .subject(memberId)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }
    
    // Refresh Token 생성
    public String generateRefreshToken(String memberId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtProperties.getRefreshToken().getExpiration());
        
        return Jwts.builder()
                .subject(memberId)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }
    
    // 토큰에서 사용자 ID 추출
    public String getMemberIdFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (Exception e) {
            log.error("토큰에서 사용자 ID 추출 실패: {}", e.getMessage());
            return null;
        }
    }
    
    // 토큰 유효성 검증
    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("만료된 토큰: {}", e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 토큰: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            log.warn("잘못된 형식의 토큰: {}", e.getMessage());
            return false;
        } catch (SecurityException e) {
            log.warn("잘못된 JWT 서명: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 토큰: {}", e.getMessage());
            return false;
        }
    }
    
    // 토큰 만료 여부 확인
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
    
    // 토큰의 남은 유효시간(ms) 반환
    public long getTokenExpirationTime(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getExpiration().getTime() - System.currentTimeMillis();
        } catch (Exception e) {
            return 0;
        }
    }
} 