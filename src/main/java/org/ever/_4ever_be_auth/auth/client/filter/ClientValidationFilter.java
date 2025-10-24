package org.ever._4ever_be_auth.auth.client.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.ever._4ever_be_auth.auth.client.exception.ClientValidationException;
import org.ever._4ever_be_auth.auth.client.service.ClientValidationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@AllArgsConstructor
public class ClientValidationFilter extends OncePerRequestFilter {
    private static final Set<String> TARGET_PATHS = Set.of("/login", "/oauth/authorize");
    private final ClientValidationService clientValidationService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getServletPath();
        if (TARGET_PATHS.contains(path)) {
            String clientId = request.getParameter("client_id");
            String redirectUri = request.getParameter("redirect_uri");
            try {
                clientValidationService.validateClient(clientId, redirectUri);
            } catch (ClientValidationException ex) {
                response.sendError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
