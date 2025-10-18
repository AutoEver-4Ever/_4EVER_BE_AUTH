package org.ever._4ever_be_auth.user.service;

import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_auth.common.exception.BusinessException;
import org.ever._4ever_be_auth.common.exception.ErrorCode;
import org.ever._4ever_be_auth.user.dto.CreateUserRequestDto;
import org.ever._4ever_be_auth.user.entity.User;
import org.ever._4ever_be_auth.user.enums.Permission;
import org.ever._4ever_be_auth.user.enums.UserRole;
import org.ever._4ever_be_auth.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User createUser(CreateUserRequestDto requestDto, UserRole requesterUserRole) {
        if (!requesterUserRole.getPermissions().contains(Permission.CREATE_USER)) {
            throw new BusinessException(
                    ErrorCode.FORBIDDEN_OPERATION,
                    String.format("사용자 생성 권한이 없습니다. (missing: %s)", Permission.CREATE_USER.name()));
        }

        String loginEmail = requestDto.getContactEmail().toLowerCase();
        if (userRepository.existsByLoginEmail(loginEmail)) {
            throw new BusinessException(
                    ErrorCode.DUPLICATE_RESOURCE,
                    "이미 등록된 이메일 입니다: " + loginEmail
            );
        }

        String rawInitialPassword = generateInitialPassword();
        String encodedPassword = passwordEncoder.encode(rawInitialPassword);

        User newUser = User.create(
                loginEmail,
                encodedPassword,
                requestDto.getUserRole()
        );
        return userRepository.save(newUser);
    }

    private String generateInitialPassword() {
        return UUID.randomUUID().toString();
    }
}
