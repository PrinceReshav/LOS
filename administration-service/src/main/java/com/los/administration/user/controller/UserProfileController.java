package com.los.administration.user.controller;

import com.los.administration.common.dto.ApiResponse;
import com.los.administration.user.dto.UserProfileResponse;
import com.los.administration.user.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<UserProfileResponse> getMyProfile() {

        return ApiResponse.success(
                userService.getMyProfile(),
                "Profile fetched successfully"
        );
    }
}