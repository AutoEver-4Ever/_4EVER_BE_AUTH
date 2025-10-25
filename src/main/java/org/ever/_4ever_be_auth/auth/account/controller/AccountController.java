package org.ever._4ever_be_auth.auth.account.controller;

import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_auth.auth.account.service.AccountService;
import org.ever._4ever_be_auth.common.exception.BusinessException;
import org.ever._4ever_be_auth.common.exception.ErrorCode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    // 비밀번호 변경 페이지
    @GetMapping("/password/reset")
    public String passwordResetRequestPage() {
        return "password-reset-request";
    }

    // 비밀번호 변경을 위한 메일 송부
    @PostMapping("/password/reset")
    public String requestPasswordReset(
            @RequestParam("email") String email,
            RedirectAttributes redirectAttributes
    ) {
        try {
            accountService.sendResetLink(email.trim());
            redirectAttributes.addFlashAttribute("message", "입력하신 이메일로 재설정 링크를 전송했습니다.");
            return "redirect:/password/reset?sent";
        } catch (BusinessException e) {
            if (e.getErrorCode() == ErrorCode.USER_NOT_FOUND) {
                redirectAttributes.addFlashAttribute("error", "등록되지 않은 이메일입니다.");
                return "redirect:/password/reset?error";
            }
            throw e;
        }
    }

    // 비밀번호 confirm page
    @GetMapping("/password/reset/confirm")
    public String confirmResetToken(
            @RequestParam("token") String token,
            Model model
    ) {
        model.addAttribute("token", token);
        return "password-reset-confirm";
    }

    // 비밀번호 confirm post
    @PostMapping("/password/reset/confirm")
    public String resetPassword(
            @RequestParam("token") String token,
            @RequestParam("newPassword") String newPassword,
            RedirectAttributes redirectAttributes
    ) {
        accountService.resetPassword(token, newPassword);
        redirectAttributes.addFlashAttribute("message", "비밀번호가 변경되었습니다. 새 비밀번호로 로그인해 주세요");
        return "redirect:/login?success";
    }
}
