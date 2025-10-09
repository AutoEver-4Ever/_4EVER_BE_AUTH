package org.ever._4ever_be_auth.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "사용자 인증 응답 정보")
public class AuthResponseDto {

    @Schema(description = "사용자 ID", example = "abc123-def456")
    private String userId;

    @Schema(description = "이메일", example = "user@example.com")
    private String email;

    @Schema(description = "사용자 이름", example = "홍길동")
    private String username;

    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phoneNumber;

    @Schema(description = "역할", example = "USER", allowableValues = {"USER", "ADMIN", "MANAGER"})
    private String role;

    @Schema(description = "계정 상태", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE", "SUSPENDED", "DELETED"})
    private String status;

    @Schema(description = "생성 시각", example = "2025-10-08T12:34:56")
    private LocalDateTime createdAt;

    @Schema(description = "수정 시각", example = "2025-10-08T12:34:56")
    private LocalDateTime updatedAt;

    @Schema(description = "마지막 로그인 시각", example = "2025-10-08T12:34:56")
    private LocalDateTime lastLoginAt;
}
