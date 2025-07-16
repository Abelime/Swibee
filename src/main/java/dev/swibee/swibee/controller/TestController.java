package dev.swibee.swibee.controller;

import dev.swibee.swibee.dto.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {
    
    // 인증이 필요한 테스트 엔드포인트
    @GetMapping("/protected")
    public ResponseEntity<ApiResponse<Map<String, Object>>> protectedEndpoint() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        Map<String, Object> data = new HashMap<>();
        data.put("message", "인증된 사용자만 접근 가능합니다.");
        data.put("username", authentication.getName());
        data.put("authorities", authentication.getAuthorities());
        
        return ResponseEntity.ok(ApiResponse.success("인증 성공", data));
    }
    
    // 인증이 불필요한 테스트 엔드포인트
    @GetMapping("/public")
    public ResponseEntity<ApiResponse<String>> publicEndpoint() {
        return ResponseEntity.ok(ApiResponse.success("누구나 접근 가능합니다."));
    }
} 