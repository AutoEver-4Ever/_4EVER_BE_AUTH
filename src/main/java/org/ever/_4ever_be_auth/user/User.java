package org.ever._4ever_be_auth.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_auth.common.entity.TimeStamp;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User extends TimeStamp {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "user_id", nullable = false, updatable = false, unique = true, length = 36)
    private String userId;

    @Column(name = "login_email", nullable = false, unique = true, length = 320)
    private String loginEmail;      // 사용자의 로그인 이메일

    @Column(nullable = false, length = 50)
    private String username;    // 사용자(담당자)의 이름

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, unique = true, length = 320)
    private String email;       // 사용자의 연락 이메일

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole userRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserType userType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus userStatus;

    @Column(name = "password_last_changed_at")
    private LocalDateTime passwordLastChangedAt;  // null 이면 최초 비밀번호 변경 필요

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder(access = AccessLevel.PRIVATE)
    public User(String userId,
                String loginEmail,
                String username,
                String password,
                String email,
                String phoneNumber,
                UserRole userRole,
                UserType userType,
                UserStatus userStatus,
                LocalDateTime passwordLastChangedAt) {
        this.userId = userId;
        this.loginEmail = loginEmail;
        this.password = password;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.userRole = userRole;
        this.userType = userType;
        this.userStatus = userStatus;
        this.passwordLastChangedAt = passwordLastChangedAt;
    }
}
