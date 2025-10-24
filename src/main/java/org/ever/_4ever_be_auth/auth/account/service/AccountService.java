package org.ever._4ever_be_auth.auth.account.service;

public interface AccountService {
    // 비밀번호 재설정 이메일 전송
    void sendResetLink(String email);

    // 재설정 토큰으로 새 비밀번호 적용
    void resetPassword(String token, String newPassword);
}
