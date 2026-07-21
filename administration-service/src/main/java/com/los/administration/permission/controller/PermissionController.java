package com.los.administration.permission.controller;

import com.los.administration.common.dto.ApiResponse;
import com.los.administration.permission.model.Permission;
import com.los.administration.permission.service.PermissionService;
import com.los.administration.security.annotation.RequiresPermission;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Permissions are module-level master data (one row per module: USER,
 * ROLE, PROFILE, ...). They are almost always seeded rather than created
 * ad hoc, but full CRUD is exposed so a new module can be registered
 * without a redeploy - matching the "no hardcoded rules" philosophy used
 * across the LOS admin suite.
 */
@RestController
@RequestMapping("/admin/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PreAuthorize("hasRole('ADMIN')")
    @RequiresPermission(object = "PERMISSION", action = "CREATE")
    @PostMapping
    public ApiResponse<Permission> create(@Valid @RequestBody Permission permission) {
        return ApiResponse.success(
                permissionService.create(permission),
                "Permission created successfully"
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequiresPermission(object = "PERMISSION", action = "UPDATE")
    @PutMapping("/{id}")
    public ApiResponse<Permission> update(
            @PathVariable String id,
            @RequestBody Permission permission
    ) {
        return ApiResponse.success(
                permissionService.update(id, permission),
                "Permission updated successfully"
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequiresPermission(object = "PERMISSION", action = "READ")
    @GetMapping("/{id}")
    public ApiResponse<Permission> getById(@PathVariable String id) {
        return ApiResponse.success(
                permissionService.getById(id),
                "Permission fetched successfully"
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequiresPermission(object = "PERMISSION", action = "READ")
    @GetMapping
    public ApiResponse<List<Permission>> getAll() {
        return ApiResponse.success(
                permissionService.getAll(),
                "Permissions fetched successfully"
        );
    }
}
