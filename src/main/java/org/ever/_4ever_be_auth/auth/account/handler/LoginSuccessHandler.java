package org.ever._4ever_be_auth.auth.account.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_auth.user.entity.User;
import org.ever._4ever_be_auth.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private static final String OAUTH_REQUEST_SESSION_KEY = "OAUTH2_AUTHORIZATION_REQUEST";

    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.removeAttribute(OAUTH_REQUEST_SESSION_KEY);
        }

        if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            User user = userRepository.findByLoginEmail(userDetails.getUsername()).orElse(null);
            if (user != null && user.getPasswordLastChangedAt() == null) {
                getRedirectStrategy().sendRedirect(request, response, "/password/change");
                return;
            }
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }

}
