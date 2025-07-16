package dev.swibee.swibee.service;

import dev.swibee.swibee.config.JwtProperties;
import dev.swibee.swibee.dto.auth.SignInRequest;
import dev.swibee.swibee.dto.auth.SignUpRequest;
import dev.swibee.swibee.dto.auth.TokenResponse;
import dev.swibee.swibee.entity.Member;
import dev.swibee.swibee.repository.MemberRepository;
import dev.swibee.swibee.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final JwtProperties jwtProperties;
    
    // 회원가입
    @Transactional
    public void signUp(SignUpRequest request) {
        // 비밀번호 확인 검증
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        
        // 아이디 중복 체크
        if (memberRepository.existsByMemberIdAndIsDeletedFalse(request.getMemberId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        
        // 휴대폰 번호 중복 체크 (입력된 경우만)
        if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            if (memberRepository.existsByPhoneAndIsDeletedFalse(request.getPhone())) {
                throw new IllegalArgumentException("이미 사용 중인 휴대폰 번호입니다.");
            }
        }
        
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        
        // UUID 생성
        String uuid = UUID.randomUUID().toString();
        
        // 회원 생성
        Member member = Member.builder()
                .memberId(request.getMemberId())
                .password(encodedPassword)
                .name(request.getName())
                .phone(request.getPhone())
                .uuid(uuid)
                .build();
        
        memberRepository.save(member);
        
        log.info("회원가입 완료 - memberId: {}", request.getMemberId());
    }
    
    // 로그인
    @Transactional
    public TokenResponse signIn(SignInRequest request) {
        // 회원 조회
        Member member = memberRepository.findByMemberIdAndIsDeletedFalse(request.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 잘못되었습니다."));
        
        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 잘못되었습니다.");
        }
        
        // 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(member.getMemberId());
        String refreshToken = jwtUtil.generateRefreshToken(member.getMemberId());
        
        // Refresh Token Redis에 저장
        refreshTokenService.saveRefreshToken(member.getMemberId(), refreshToken);
        
        long expiresIn = jwtProperties.getAccessToken().getExpiration() / 1000; // 초 단위로 변환
        
        log.info("로그인 성공 - memberId: {}", member.getMemberId());
        
        return new TokenResponse(accessToken, refreshToken, expiresIn);
    }
    
    // 토큰 갱신
    @Transactional
    public TokenResponse refreshToken(String refreshToken) {
        // Refresh Token에서 사용자 ID 추출
        String memberId = jwtUtil.getMemberIdFromToken(refreshToken);
        if (memberId == null) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다.");
        }
        
        // 회원 존재 여부 확인
        Member member = memberRepository.findByMemberIdAndIsDeletedFalse(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        
        // Refresh Token 유효성 검증
        if (!refreshTokenService.isValidRefreshToken(memberId, refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다.");
        }
        
        // 새로운 토큰 생성
        String newAccessToken = jwtUtil.generateAccessToken(memberId);
        String newRefreshToken = jwtUtil.generateRefreshToken(memberId);
        
        // 새로운 Refresh Token Redis에 저장
        refreshTokenService.saveRefreshToken(memberId, newRefreshToken);
        
        long expiresIn = jwtProperties.getAccessToken().getExpiration() / 1000; // 초 단위로 변환
        
        log.info("토큰 갱신 완료 - memberId: {}", memberId);
        
        return new TokenResponse(newAccessToken, newRefreshToken, expiresIn);
    }
    
    // 로그아웃
    @Transactional
    public void signOut(String memberId) {
        // Refresh Token 삭제
        refreshTokenService.deleteRefreshToken(memberId);
        
        log.info("로그아웃 완료 - memberId: {}", memberId);
    }
    
    // 모든 기기에서 로그아웃
    @Transactional
    public void signOutFromAllDevices(String memberId) {
        // 모든 Refresh Token 삭제
        refreshTokenService.deleteAllRefreshTokens(memberId);
        
        log.info("모든 기기에서 로그아웃 완료 - memberId: {}", memberId);
    }
    
    // 아이디 중복 체크
    public boolean isAvailableMemberId(String memberId) {
        return !memberRepository.existsByMemberIdAndIsDeletedFalse(memberId);
    }
    
    // 휴대폰 번호 중복 체크
    public boolean isAvailablePhone(String phone) {
        return !memberRepository.existsByPhoneAndIsDeletedFalse(phone);
    }
} 