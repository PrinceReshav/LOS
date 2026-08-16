package com.los.administration.profilepermission.controller;

import com.los.administration.common.dto.ApiResponse;
import com.los.administration.profilepermission.dto.ProfilePermissionMatrixRequest;
import com.los.administration.profilepermission.dto.ProfilePermissionMatrixResponse;
import com.los.administration.profilepermission.service.ProfilePermissionService;
import com.los.administration.security.annotation.RequiresPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/profiles/{profileId}/permissions")
@RequiredArgsConstructor
public class ProfilePermissionController {

    private final ProfilePermissionService profilePermissionService;

    @PreAuthorize("hasRole('ADMIN')")
    @RequiresPermission(object = "PERMISSION", action = "READ")
    @GetMapping
    public ApiResponse<ProfilePermissionMatrixResponse> getMatrix(@PathVariable String profileId) {
        return ApiResponse.success(
                profilePermissionService.getMatrix(profileId),
                "Permission matrix fetched successfully"
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequiresPermission(object = "PERMISSION", action = "EDIT")
    @PutMapping
    public ApiResponse<ProfilePermissionMatrixResponse> saveMatrix(
            @PathVariable String profileId,
            @RequestBody ProfilePermissionMatrixRequest request
    ) {
        return ApiResponse.success(
                profilePermissionService.saveMatrix(profileId, request.getEntries()),
                "Permission matrix updated successfully"
        );
    }
}
