package org.sujan.stockflow_backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.sujan.stockflow_backend.security.UserPrincipal;

import java.io.IOException;

@Component
public class PasswordChangeFilter extends OncePerRequestFilter {

    // Endpoints a user must still be able to reach even while locked
    private static final String CHANGE_PASSWORD_PATH = "/api/auth/change-password";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal principal) {
            boolean isChangePasswordRequest = request.getRequestURI().equals(CHANGE_PASSWORD_PATH);

            if (principal.getUser().isMustChangePassword() && !isChangePasswordRequest) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write(
                    "{\"error\": \"PASSWORD_CHANGE_REQUIRED\", \"message\": \"You must change your temporary password before continuing.\"}"
                );
                return; // stop here — do NOT call filterChain.doFilter()
            }
        }

        filterChain.doFilter(request, response);
    }
}