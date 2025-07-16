package dev.swibee.swibee.controller;

import dev.swibee.swibee.dto.auth.RefreshTokenRequest;
import dev.swibee.swibee.dto.auth.SignInRequest;
import dev.swibee.swibee.dto.auth.SignUpRequest;
import dev.swibee.swibee.dto.auth.TokenResponse;
import dev.swibee.swibee.dto.common.ApiResponse;
import dev.swibee.swibee.service.AuthService;
import dev.swibee.swibee.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    private final JwtUtil jwtUtil;
    
    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signUp(@RequestBody SignUpRequest request) {
        try {
            authService.signUp(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("회원가입이 완료되었습니다.", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("회원가입 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("회원가입 중 오류가 발생했습니다."));
        }
    }
    
    // 로그인
    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<TokenResponse>> signIn(@Valid @RequestBody SignInRequest request) {
        try {
            TokenResponse tokenResponse = authService.signIn(request);
            return ResponseEntity.ok(ApiResponse.success("로그인이 완료되었습니다.", tokenResponse));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("로그인 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("로그인 중 오류가 발생했습니다."));
        }
    }
    
    // 토큰 갱신
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            TokenResponse tokenResponse = authService.refreshToken(request.getRefreshToken());
            return ResponseEntity.ok(ApiResponse.success("토큰이 갱신되었습니다.", tokenResponse));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("토큰 갱신 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("토큰 갱신 중 오류가 발생했습니다."));
        }
    }
    
    // 로그아웃
    @PostMapping("/signout")
    public ResponseEntity<ApiResponse<Void>> signOut(HttpServletRequest request) {
        try {
            String accessToken = getTokenFromRequest(request);
            if (accessToken == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Access Token이 필요합니다."));
            }
            
            String memberId = jwtUtil.getMemberIdFromToken(accessToken);
            if (memberId == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("유효하지 않은 토큰입니다."));
            }
            
            authService.signOut(memberId);
            return ResponseEntity.ok(ApiResponse.success("로그아웃이 완료되었습니다.", null));
        } catch (Exception e) {
            log.error("로그아웃 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("로그아웃 중 오류가 발생했습니다."));
        }
    }
    
    // 모든 기기에서 로그아웃
    @PostMapping("/signout-all")
    public ResponseEntity<ApiResponse<Void>> signOutFromAllDevices(HttpServletRequest request) {
        try {
            String accessToken = getTokenFromRequest(request);
            if (accessToken == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Access Token이 필요합니다."));
            }
            
            String memberId = jwtUtil.getMemberIdFromToken(accessToken);
            if (memberId == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("유효하지 않은 토큰입니다."));
            }
            
            authService.signOutFromAllDevices(memberId);
            return ResponseEntity.ok(ApiResponse.success("모든 기기에서 로그아웃이 완료되었습니다.", null));
        } catch (Exception e) {
            log.error("모든 기기 로그아웃 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("로그아웃 중 오류가 발생했습니다."));
        }
    }
    
    // 아이디 중복 체크
    @GetMapping("/check-member-id")
    public ResponseEntity<ApiResponse<Boolean>> checkMemberId(@RequestParam String memberId) {
        try {
            boolean isAvailable = authService.isAvailableMemberId(memberId);
            String message = isAvailable ? "사용 가능한 아이디입니다." : "이미 사용 중인 아이디입니다.";
            return ResponseEntity.ok(ApiResponse.success(message, isAvailable));
        } catch (Exception e) {
            log.error("아이디 중복 체크 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("아이디 중복 체크 중 오류가 발생했습니다."));
        }
    }
    
    // 휴대폰 번호 중복 체크
    @GetMapping("/check-phone")
    public ResponseEntity<ApiResponse<Boolean>> checkPhone(@RequestParam String phone) {
        try {
            boolean isAvailable = authService.isAvailablePhone(phone);
            String message = isAvailable ? "사용 가능한 휴대폰 번호입니다." : "이미 사용 중인 휴대폰 번호입니다.";
            return ResponseEntity.ok(ApiResponse.success(message, isAvailable));
        } catch (Exception e) {
            log.error("휴대폰 번호 중복 체크 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("휴대폰 번호 중복 체크 중 오류가 발생했습니다."));
        }
    }
    
    // 헤더에서 토큰 추출
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
} 