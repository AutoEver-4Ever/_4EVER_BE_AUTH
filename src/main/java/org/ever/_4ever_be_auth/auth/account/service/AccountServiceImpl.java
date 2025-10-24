package org.ever._4ever_be_auth.auth.account.service;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_auth.auth.account.entity.PasswordResetToken;
import org.ever._4ever_be_auth.auth.account.repository.PasswordResetTokenRepository;
import org.ever._4ever_be_auth.common.exception.BusinessException;
import org.ever._4ever_be_auth.common.exception.ErrorCode;
import org.ever._4ever_be_auth.user.entity.User;
import org.ever._4ever_be_auth.user.repository.UserRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private static final Duration TOKEN_TTL = Duration.ofMinutes(15);

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final PasswordResetMailFactory mailFactory;

    @Override
    public void sendResetLink(String email) {
        String normalizedEmail = email.trim();
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        tokenRepository.deleteByUserId(user.getUserId());

        PasswordResetToken token = tokenRepository.save(
                PasswordResetToken.create(
                        UUID.randomUUID().toString(),
                        user.getUserId(),
                        LocalDateTime.now().plus(TOKEN_TTL)
                )
        );

        try {
            mailSender.send(mailFactory.createMessage(user.getEmail(), token.getToken()));
        } catch (MessagingException e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "비밀번호 재설정 메일 발송에 실패했습니다.", e);
        }

        log.info("[INFO] 비밀번호 재설정 메일 전송 완료 -> {}", user.getEmail());
    }

    @Override
    public void resetPassword(String rawToken, String newPassword) {
        LocalDateTime now = LocalDateTime.now();
        PasswordResetToken token = tokenRepository
                .findByTokenAndUsedFalseAndExpiresAtAfter(rawToken, now)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));

        if (token.isExpired(now)) {
            throw new BusinessException(ErrorCode.TOKEN_EXPIRED);
        }

        User user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.updatePassword(passwordEncoder.encode(newPassword), now);
        token.markAsUsed();

        log.info("[INFO] 비밀번호 재설정 완료 -> {}", user.getLoginEmail());
    }

}
