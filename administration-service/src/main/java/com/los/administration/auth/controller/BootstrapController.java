package com.los.administration.auth.controller;

import com.los.administration.common.dto.ApiResponse;
import com.los.administration.common.exception.BadRequestException;
import com.los.administration.profile.repository.ProfileRepository;
import com.los.administration.role.repository.RoleRepository;
import com.los.administration.user.dto.UserCreateRequest;
import com.los.administration.user.dto.UserResponse;
import com.los.administration.user.service.UserCreationService;
import com.los.administration.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/bootstrap")
@RequiredArgsConstructor
public class BootstrapController {

    private final UserCreationService userCreationService;
    private final UserService userService;
    private final RoleRepository roleRepository;
    private final ProfileRepository profileRepository;

    @Value("${bootstrap.admin-secret}")
    private String bootstrapSecret;

    @PostMapping("/admin")
    public ApiResponse<UserResponse> createInitialAdmin(
            @RequestHeader("X-BOOTSTRAP-SECRET") String secret,
            @RequestBody @Valid UserCreateRequest request
    ) {
        if (!bootstrapSecret.equals(secret)) {
            throw new BadRequestException("INVALID_BOOTSTRAP_SECRET");
        }

        if (userService.hasAnyUser()) {
            throw new BadRequestException("ADMIN_ALREADY_EXISTS");
        }

        UserResponse admin =
                userCreationService.createUser(request, "BOOTSTRAP");

        return ApiResponse.success(admin, "Initial admin created");
    }
}