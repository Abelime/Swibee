package dev.swibee.swibee.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "member_id", unique = true, nullable = false, length = 50)
    private String memberId;
    
    @Column(name = "password", length = 255)
    private String password;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "phone", length = 20)
    private String phone;
    
    @Column(name = "uuid", length = 36)
    private String uuid;
    
    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    // 비밀번호 업데이트 메서드
    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
    
    // 회원 정보 업데이트 메서드
    public void updateInfo(String name, String phone) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }
        if (phone != null && !phone.trim().isEmpty()) {
            this.phone = phone;
        }
    }
    
    // 소프트 삭제 메서드
    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }
    
    // 삭제 취소 메서드
    public void restore() {
        this.isDeleted = false;
        this.deletedAt = null;
    }
} 