package org.sujan.stockflow_backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.sujan.stockflow_backend.security.SecurityErrorWriter;
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
                SecurityErrorWriter.write(response, 403, "PASSWORD_CHANGE_REQUIRED",
                        "Please change your temporary password to continue.");
                return; // stop here — do NOT call filterChain.doFilter()
            }
        }

        filterChain.doFilter(request, response);
    }
}