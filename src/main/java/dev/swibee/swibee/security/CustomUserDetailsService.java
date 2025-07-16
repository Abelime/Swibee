package dev.swibee.swibee.security;

import dev.swibee.swibee.entity.Member;
import dev.swibee.swibee.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    
    private final MemberRepository memberRepository;
    
    @Override
    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
        Member member = memberRepository.findByMemberIdAndIsDeletedFalse(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + memberId));
        
        return User.builder()
                .username(member.getMemberId())
                .password(member.getPassword())
                .authorities(new ArrayList<>()) // 현재는 권한이 없으므로 빈 리스트
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(member.getIsDeleted())
                .build();
    }
} 