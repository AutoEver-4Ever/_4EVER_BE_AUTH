package org.ever._4ever_be_auth.auth.service;

import org.ever._4ever_be_auth.auth.dto.response.AuthResponseDto;
import org.ever._4ever_be_auth.auth.vo.AuthRequestVo;

import java.util.List;

/**
 * 인증 서비스 인터페이스
 */
public interface AuthService {

    /**
     * 사용자 등록
     */
    AuthResponseDto registerUser(AuthRequestVo request);

    /**
     * 사용자 정보 조회
     */
    AuthResponseDto getUser(String userId);

    /**
     * 모든 사용자 정보 조회
     */
    List<AuthResponseDto> getAllUsers();

    /**
     * 사용자 삭제
     */
    AuthResponseDto deleteUser(String userId);

    /**
     * 이메일로 사용자 조회
     */
    AuthResponseDto getUserByEmail(String email);
}
