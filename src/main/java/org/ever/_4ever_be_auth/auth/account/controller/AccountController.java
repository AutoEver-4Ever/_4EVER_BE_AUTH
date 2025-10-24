package org.ever._4ever_be_auth.auth.account.controller;

import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_auth.auth.account.service.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    // 로그인
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/password/reset")
    public String passwordResetRequestPage() {
        return "password-reset-request";
    }

    // 비밀번호
    @PostMapping("/password/reset")
    public String requestPasswordReset(
            @RequestParam("email") String email,
            RedirectAttributes redirectAttributes
    ) {
        accountService.sendResetLink(email.trim());
        redirectAttributes.addFlashAttribute("message", "입력하신 이메일로 재설정 링크를 전송했습니다.");
        return "redirect:/password/reset?sent";
    }
}
