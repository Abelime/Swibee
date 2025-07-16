package dev.swibee.swibee.repository;

import dev.swibee.swibee.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    
    // 삭제되지 않은 회원 중에서 회원 ID로 조회
    @Query("SELECT m FROM Member m WHERE m.memberId = :memberId AND m.isDeleted = false")
    Optional<Member> findByMemberIdAndIsDeletedFalse(@Param("memberId") String memberId);
    
    // 회원 ID 중복 체크 (삭제되지 않은 회원만)
    @Query("SELECT COUNT(m) > 0 FROM Member m WHERE m.memberId = :memberId AND m.isDeleted = false")
    boolean existsByMemberIdAndIsDeletedFalse(@Param("memberId") String memberId);
    
    // UUID로 회원 조회 (삭제되지 않은 회원만)
    @Query("SELECT m FROM Member m WHERE m.uuid = :uuid AND m.isDeleted = false")
    Optional<Member> findByUuidAndIsDeletedFalse(@Param("uuid") String uuid);
    
    // 휴대폰 번호로 회원 조회 (삭제되지 않은 회원만)
    @Query("SELECT m FROM Member m WHERE m.phone = :phone AND m.isDeleted = false")
    Optional<Member> findByPhoneAndIsDeletedFalse(@Param("phone") String phone);
    
    // 휴대폰 번호 중복 체크 (삭제되지 않은 회원만)
    @Query("SELECT COUNT(m) > 0 FROM Member m WHERE m.phone = :phone AND m.isDeleted = false")
    boolean existsByPhoneAndIsDeletedFalse(@Param("phone") String phone);
} 