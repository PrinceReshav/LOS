package com.los.administration.profile.controller;

import com.los.administration.common.dto.ApiResponse;
import com.los.administration.profile.model.Profile;
import com.los.administration.profile.service.ProfileService;
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
    @PostMapping
    public ApiResponse<Profile> createProfile(@Valid @RequestBody Profile profile) {
        Profile saved = profileService.createProfile(profile);
        return ApiResponse.success(saved, "Profile created successfully");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ApiResponse<List<Profile>> getAllProfiles() {
        return ApiResponse.success(
                profileService.getAllProfiles(),
                "Profiles fetched successfully"
        );
    }
}
