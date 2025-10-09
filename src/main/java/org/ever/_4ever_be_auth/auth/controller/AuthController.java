package org.ever._4ever_be_auth.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_auth.common.response.ApiResponse;
import org.ever._4ever_be_auth.auth.dto.request.AuthRequestDto;
import org.ever._4ever_be_auth.auth.dto.response.AuthResponseDto;
import org.ever._4ever_be_auth.auth.service.AuthService;
import org.ever._4ever_be_auth.auth.vo.AuthRequestVo;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<AuthResponseDto> registerUser(@Valid @RequestBody AuthRequestDto requestDto) {
        log.info("사용자 등록 요청 수신 - email: {}, username: {}", requestDto.getEmail(), requestDto.getUsername());

        AuthRequestVo requestVo = new AuthRequestVo(
            requestDto.getEmail(),
            requestDto.getUsername(),
            requestDto.getPassword(),
            requestDto.getPhoneNumber(),
            requestDto.getRole()
        );

        AuthResponseDto responseDto = authService.registerUser(requestVo);
        return ApiResponse.success(responseDto, "사용자가 성공적으로 등록되었습니다.", HttpStatus.CREATED);
    }

    @GetMapping("/users/{userId}")
    public ApiResponse<AuthResponseDto> getUser(@PathVariable String userId) {
        log.info("사용자 정보 조회 요청 - userId: {}", userId);
        AuthResponseDto responseDto = authService.getUser(userId);
        return ApiResponse.success(responseDto, "사용자 정보 조회 성공", HttpStatus.OK);
    }

    @GetMapping("/users")
    public ApiResponse<List<AuthResponseDto>> getAllUsers() {
        log.info("전체 사용자 정보 조회 요청");
        List<AuthResponseDto> responseDtoList = authService.getAllUsers();
        return ApiResponse.success(responseDtoList, "전체 사용자 정보 조회 성공", HttpStatus.OK);
    }

    @GetMapping("/users/email/{email}")
    public ApiResponse<AuthResponseDto> getUserByEmail(@PathVariable String email) {
        log.info("이메일로 사용자 조회 요청 - email: {}", email);
        AuthResponseDto responseDto = authService.getUserByEmail(email);
        return ApiResponse.success(responseDto, "사용자 정보 조회 성공", HttpStatus.OK);
    }

    @DeleteMapping("/users/{userId}")
    public ApiResponse<AuthResponseDto> deleteUser(@PathVariable String userId) {
        log.info("사용자 삭제 요청 - userId: {}", userId);
        AuthResponseDto responseDto = authService.deleteUser(userId);
        return ApiResponse.success(responseDto, "사용자가 성공적으로 삭제되었습니다.", HttpStatus.OK);
    }

    @GetMapping("/health")
    public ApiResponse<String> healthCheck() {
        return ApiResponse.success("Auth Service is running", "헬스 체크 성공", HttpStatus.OK);
    }
}
