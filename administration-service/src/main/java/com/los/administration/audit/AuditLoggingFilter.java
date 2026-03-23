package com.los.administration.audit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AuditLoggingFilter extends OncePerRequestFilter {

    private final AuditLogService auditLogService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } finally {

            Authentication auth =
                    SecurityContextHolder.getContext().getAuthentication();

            if (auth == null || !auth.isAuthenticated()) {
                return; // unauthenticated → ignore
            }

            String userId = auth.getName(); // JWT subject
            String role = auth.getAuthorities().stream()
                    .findFirst()
                    .map(a -> a.getAuthority())
                    .orElse("UNKNOWN");

            AuditLogEntry entry = AuditLogEntry.builder()
                    .userId(userId)
                    .role(role)
                    .httpMethod(request.getMethod())
                    .endpoint(request.getRequestURI())
                    .action(resolveAction(request))
                    .httpStatus(response.getStatus())
                    .success(response.getStatus() < 400)
                    .timestamp(LocalDateTime.now())
                    .build();

            auditLogService.log(entry);
        }
    }

    private String resolveAction(HttpServletRequest request) {

        String uri = request.getRequestURI();
        String method = request.getMethod();

        if (uri.contains("/login")) return "LOGIN";
        if (uri.contains("/password")) return "PASSWORD_ACTION";
        if (uri.contains("/users") && method.equals("POST")) return "CREATE_USER";
        if (uri.contains("/users") && method.equals("PATCH")) return "UPDATE_USER";

        return method + " " + uri;
    }
}