package com.los.administration.auth.controller;

import com.los.administration.auth.dto.*;
import com.los.administration.auth.service.AuthService;
import com.los.administration.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/password/setup")
    public ApiResponse<PasswordSetupResponse> setupPassword(
            @RequestBody @Valid PasswordSetupRequest request
    ) {
        return ApiResponse.success(
                authService.setupPassword(request),
                "Password setup successful"
        );
    }


    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(
            @RequestBody @Valid LoginRequest request
    ) {
        return ApiResponse.success(
                authService.login(request),
                "Login successful"
        );
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/users/{userId}/password/reset")
    public ApiResponse<Void> adminResetPassword(
            @PathVariable String userId) {

        authService.adminInitiatePasswordReset(userId);
        return ApiResponse.success(null, "Password reset link sent");
    }

    @PostMapping("/password/forgot")
    public ApiResponse<Void> forgotPassword(
            @RequestBody @Valid ForgotPasswordRequest request) {

        authService.initiatePasswordReset(request.getUsernameOrEmail());
        return ApiResponse.success(null, "If the account exists, a reset link has been sent");
    }



}
