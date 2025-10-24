package org.ever._4ever_be_auth.auth.client.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.ever._4ever_be_auth.auth.client.exception.ClientValidationException;
import org.ever._4ever_be_auth.auth.client.service.ClientValidationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@AllArgsConstructor
public class ClientValidationFilter extends OncePerRequestFilter {

    private static final String LOGIN_PATH = "/login";
    private static final String AUTHORIZATION_PATH = "/oauth2/authorize";

    private final ClientValidationService clientValidationService;
    private final RequestCache requestCache = new HttpSessionRequestCache();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getServletPath(); // 요청(request)에 대한 서블릿 경로를 가져옴, ex) /oauth2/authorize

        if (AUTHORIZATION_PATH.equals(path)) {
            if (!validateParam(request, response)) {
                return;
            }
        } else if (LOGIN_PATH.equals(path)) {
            SavedRequest savedRequest = requestCache.getRequest(request, response);

            if (savedRequest == null) {
                response.sendError(HttpStatus.BAD_REQUEST.value(), "직접 접근할 수 없는 경로입니다.");
                return;
            }
            if (!validateParam(request, response)) {
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean validateParam(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        String clientId = request.getParameter(OAuth2ParameterNames.CLIENT_ID);
        String redirectUri = request.getParameter(OAuth2ParameterNames.REDIRECT_URI);

        try {
            clientValidationService.validateClient(clientId, redirectUri);
            return true;
        } catch (ClientValidationException e) {
            response.sendError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return false;
        }
    }
}
