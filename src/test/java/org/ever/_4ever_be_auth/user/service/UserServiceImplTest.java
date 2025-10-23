package org.ever._4ever_be_auth.user.service;

import org.ever._4ever_be_auth.common.exception.BusinessException;
import org.ever._4ever_be_auth.user.dto.CreateUserRequestDto;
import org.ever._4ever_be_auth.user.entity.User;
import org.ever._4ever_be_auth.user.enums.Permission;
import org.ever._4ever_be_auth.user.enums.UserRole;
import org.ever._4ever_be_auth.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserNotificationService notificationService;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, passwordEncoder, notificationService);
    }

    // 사용자 생성 TDD
    @DisplayName("createUser는 CREATE_USER 권한이 없는 역할이면 예외를 던짐.")
    @Test
    void createUser_withoutPermission_throwsException() {
        // given
        CreateUserRequestDto requestDto = new CreateUserRequestDto(
                "sdUser@email.com", // contactEmail
                UserRole.SD_USER    // 새로 등록할 사용자의 역할
        );

        UserRole requesterRole = UserRole.MM_ADMIN; // 사용자 생성을 시도한 사람의 역할 -> 권한 없음

        // expect 403
        assertThatThrownBy(() -> userService.createUser(requestDto, requesterRole))
                .isInstanceOf(BusinessException.class)
                .extracting("detail")
                .asString()
                .contains(Permission.CREATE_USER.name());
    }

    @DisplayName("createUser는 CREATE_USER 권한이 있는 경우에만 사용자 생성을 완료함.")
    @Test
    void createUser_withPermission_savesUser() {
        // given
        CreateUserRequestDto requestDto = new CreateUserRequestDto(
                "mmUser@email.com",
                UserRole.MM_USER
        );
        UserRole requesterRole = UserRole.HRM_ADMIN;        // CREATE_USER 권한 보유

        when(userRepository.save(any(User.class))).thenReturn(mock(User.class));

        // when
        User result = userService.createUser(requestDto, requesterRole);

        // then
        verify(userRepository).save(any(User.class));
        assertThat(result).isNotNull();
    }

    @DisplayName("createUser시 생성한 로그인 이메일과 초기 비밀번호를 contactEmail로 발송함.")
    @Test
    void createUser_withPermission_sendsInitialPasswordAndEmail() {
        // given
        CreateUserRequestDto requestDto = new CreateUserRequestDto(
                "mmUser@email.com",
                UserRole.MM_USER
        );

        when(userRepository.save(any(User.class))).thenReturn(mock(User.class));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        userService.createUser(requestDto, UserRole.HRM_ADMIN);

        // then
        verify(notificationService).sendUserNotification(
                eq("mmUser@email.com"),
                eq("mmUser@everp.com"),
                anyString()
        );
    }

}
