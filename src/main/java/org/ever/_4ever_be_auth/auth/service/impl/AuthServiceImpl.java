package org.ever._4ever_be_auth.auth.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_auth.common.exception.BusinessException;
import org.ever._4ever_be_auth.common.exception.ErrorCode;
import org.ever._4ever_be_auth.auth.dto.response.AuthResponseDto;
import org.ever._4ever_be_auth.auth.service.AuthService;
import org.ever._4ever_be_auth.auth.vo.AuthRequestVo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    // 임시 저장소 (실제로는 DB 사용)
    private final List<AuthResponseDto> users = new ArrayList<>();

    @Override
    public AuthResponseDto registerUser(AuthRequestVo request) {
        log.info("사용자 등록 시작 - email: {}, username: {}", request.getEmail(), request.getUsername());

        // 이메일 중복 체크
        boolean emailExists = users.stream()
            .anyMatch(u -> u.getEmail().equals(request.getEmail()));

        if (emailExists) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE,
                "이미 등록된 이메일입니다: " + request.getEmail());
        }

        // 사용자 등록 처리
        String userId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        String role = request.getRole() != null ? request.getRole() : "USER";

        AuthResponseDto user = AuthResponseDto.builder()
            .userId(userId)
            .email(request.getEmail())
            .username(request.getUsername())
            .phoneNumber(request.getPhoneNumber())
            .role(role)
            .status("ACTIVE")
            .createdAt(now)
            .updatedAt(now)
            .lastLoginAt(now)
            .build();

        users.add(user);
        log.info("사용자 등록 완료 - userId: {}", user.getUserId());

        return user;
    }

    @Override
    public AuthResponseDto getUser(String userId) {
        log.info("사용자 정보 조회 - userId: {}", userId);

        return users.stream()
            .filter(u -> u.getUserId().equals(userId))
            .findFirst()
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                "사용자 정보를 찾을 수 없습니다: " + userId));
    }

    @Override
    public List<AuthResponseDto> getAllUsers() {
        log.info("전체 사용자 정보 조회 - 총 개수: {}", users.size());
        return new ArrayList<>(users);
    }

    @Override
    public AuthResponseDto deleteUser(String userId) {
        log.info("사용자 삭제 시작 - userId: {}", userId);

        AuthResponseDto user = users.stream()
            .filter(u -> u.getUserId().equals(userId))
            .findFirst()
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                "사용자 정보를 찾을 수 없습니다: " + userId));

        if ("DELETED".equals(user.getStatus())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT,
                "이미 삭제된 사용자입니다: " + userId);
        }

        // 사용자 삭제 처리
        users.removeIf(u -> u.getUserId().equals(userId));

        AuthResponseDto deletedUser = AuthResponseDto.builder()
            .userId(user.getUserId())
            .email(user.getEmail())
            .username(user.getUsername())
            .phoneNumber(user.getPhoneNumber())
            .role(user.getRole())
            .status("DELETED")
            .createdAt(user.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .lastLoginAt(user.getLastLoginAt())
            .build();

        users.add(deletedUser);

        log.info("사용자 삭제 완료 - userId: {}", userId);
        return deletedUser;
    }

    @Override
    public AuthResponseDto getUserByEmail(String email) {
        log.info("이메일로 사용자 조회 - email: {}", email);

        return users.stream()
            .filter(u -> u.getEmail().equals(email))
            .findFirst()
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                "사용자 정보를 찾을 수 없습니다: " + email));
    }
}
