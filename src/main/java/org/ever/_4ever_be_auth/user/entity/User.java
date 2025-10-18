package org.ever._4ever_be_auth.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_auth.common.entity.TimeStamp;
import org.ever._4ever_be_auth.user.enums.UserRole;
import org.ever._4ever_be_auth.user.enums.UserStatus;
import org.ever._4ever_be_auth.user.enums.UserType;
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
    private String loginEmail;      // 사용자의 로그인 이메일(*@everp.com)

    @Column(nullable = false, length = 100)
    private String passwordHash;

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

    @Builder(access = AccessLevel.PRIVATE)
    public User(String userId,
                String loginEmail,
                String passwordHash,
                UserRole userRole,
                UserType userType,
                UserStatus userStatus,
                LocalDateTime passwordLastChangedAt) {
        this.userId = userId;
        this.loginEmail = loginEmail;
        this.passwordHash = passwordHash;
        this.userRole = userRole;
        this.userType = userType;
        this.userStatus = userStatus;
        this.passwordLastChangedAt = passwordLastChangedAt;
    }
}
