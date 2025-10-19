package org.ever._4ever_be_auth.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserNotificationServiceImpl implements UserNotificationService {


    @Override
    public void sendUserNotification(String contactEmail, String loginEmail, String randomPassword) {
        log.info("초기 로그인 정보 발송 {} -> 로그인 이메일: {}, 비밀번호: {}", contactEmail, loginEmail, randomPassword);

        // TODO: 실제 이메일 발송 로직 작성
    }
}
