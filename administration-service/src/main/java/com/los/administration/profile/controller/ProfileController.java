package com.los.administration.profile.controller;

import com.los.administration.common.dto.ApiResponse;
import com.los.administration.profile.dto.ProfileRequest;
import com.los.administration.profile.model.Profile;
import com.los.administration.profile.service.ProfileService;
import com.los.administration.security.annotation.RequiresPermission;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PreAuthorize("hasRole('ADMIN')")
    @RequiresPermission(object = "PROFILE", action = "CREATE")
    @PostMapping
    public ApiResponse<Profile> createProfile(@Valid @RequestBody ProfileRequest request) {
        return ApiResponse.success(
                profileService.createProfile(request),
                "Profile created successfully"
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequiresPermission(object = "PROFILE", action = "EDIT")
    @PutMapping("/{profileId}")
    public ApiResponse<Profile> updateProfile(
            @PathVariable String profileId,
            @RequestBody ProfileRequest request
    ) {
        return ApiResponse.success(
                profileService.updateProfile(profileId, request),
                "Profile updated successfully"
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequiresPermission(object = "PROFILE", action = "EDIT")
    @PatchMapping("/{profileId}/activate")
    public ApiResponse<Profile> activate(@PathVariable String profileId) {
        return ApiResponse.success(
                profileService.setActive(profileId, true),
                "Profile activated successfully"
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequiresPermission(object = "PROFILE", action = "EDIT")
    @PatchMapping("/{profileId}/deactivate")
    public ApiResponse<Profile> deactivate(@PathVariable String profileId) {
        return ApiResponse.success(
                profileService.setActive(profileId, false),
                "Profile deactivated successfully"
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequiresPermission(object = "PROFILE", action = "READ")
    @GetMapping("/{profileId}")
    public ApiResponse<Profile> getById(@PathVariable String profileId) {
        return ApiResponse.success(
                profileService.getById(profileId),
                "Profile fetched successfully"
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequiresPermission(object = "PROFILE", action = "READ")
    @GetMapping
    public ApiResponse<List<Profile>> getAllProfiles() {
        return ApiResponse.success(
                profileService.getAllProfiles(),
                "Profiles fetched successfully"
        );
    }
}
