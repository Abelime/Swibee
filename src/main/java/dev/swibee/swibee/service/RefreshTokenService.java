package dev.swibee.swibee.service;

import dev.swibee.swibee.config.JwtProperties;
import dev.swibee.swibee.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtProperties jwtProperties;
    private final JwtUtil jwtUtil;
    
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    
    // Refresh Token 저장
    public void saveRefreshToken(String memberId, String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + memberId;
        long expiration = jwtProperties.getRefreshToken().getExpiration() / 1000; // 초 단위로 변환
        
        redisTemplate.opsForValue().set(key, refreshToken, expiration, TimeUnit.SECONDS);
        log.debug("Refresh Token 저장 완료 - memberId: {}", memberId);
    }
    
    // Refresh Token 조회
    public String getRefreshToken(String memberId) {
        String key = REFRESH_TOKEN_PREFIX + memberId;
        Object token = redisTemplate.opsForValue().get(key);
        return token != null ? token.toString() : null;
    }
    
    // Refresh Token 삭제 (로그아웃)
    public void deleteRefreshToken(String memberId) {
        String key = REFRESH_TOKEN_PREFIX + memberId;
        redisTemplate.delete(key);
        log.debug("Refresh Token 삭제 완료 - memberId: {}", memberId);
    }
    
    // Refresh Token 유효성 검증
    public boolean isValidRefreshToken(String memberId, String refreshToken) {
        String storedToken = getRefreshToken(memberId);
        
        if (storedToken == null) {
            log.warn("저장된 Refresh Token이 없습니다 - memberId: {}", memberId);
            return false;
        }
        
        if (!storedToken.equals(refreshToken)) {
            log.warn("Refresh Token이 일치하지 않습니다 - memberId: {}", memberId);
            return false;
        }
        
        if (!jwtUtil.isTokenValid(refreshToken)) {
            log.warn("Refresh Token이 유효하지 않습니다 - memberId: {}", memberId);
            deleteRefreshToken(memberId); // 유효하지 않은 토큰 삭제
            return false;
        }
        
        return true;
    }
    
    // 모든 디바이스에서 로그아웃 (해당 사용자의 모든 Refresh Token 삭제)
    public void deleteAllRefreshTokens(String memberId) {
        deleteRefreshToken(memberId);
        log.debug("모든 Refresh Token 삭제 완료 - memberId: {}", memberId);
    }
} 