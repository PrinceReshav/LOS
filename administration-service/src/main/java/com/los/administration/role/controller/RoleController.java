package com.los.administration.role.controller;

import com.los.administration.common.dto.ApiResponse;
import com.los.administration.role.dto.RoleRequest;
import com.los.administration.role.dto.RoleResponse;
import com.los.administration.role.service.RoleService;
import com.los.administration.security.annotation.RequiresPermission;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PreAuthorize("hasRole('ADMIN')")
    @RequiresPermission(object = "ROLE", action = "CREATE")
    @PostMapping
    public ApiResponse<RoleResponse> createRole(@Valid @RequestBody RoleRequest request) {
        return ApiResponse.success(
                roleService.createRole(request),
                "Role created successfully"
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequiresPermission(object = "ROLE", action = "EDIT")
    @PutMapping("/{roleId}")
    public ApiResponse<RoleResponse> updateRole(
            @PathVariable String roleId,
            @RequestBody RoleRequest request
    ) {
        return ApiResponse.success(
                roleService.updateRole(roleId, request),
                "Role updated successfully"
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequiresPermission(object = "ROLE", action = "EDIT")
    @PatchMapping("/{roleId}/activate")
    public ApiResponse<RoleResponse> activate(@PathVariable String roleId) {
        return ApiResponse.success(
                roleService.setActive(roleId, true),
                "Role activated successfully"
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequiresPermission(object = "ROLE", action = "EDIT")
    @PatchMapping("/{roleId}/deactivate")
    public ApiResponse<RoleResponse> deactivate(@PathVariable String roleId) {
        return ApiResponse.success(
                roleService.setActive(roleId, false),
                "Role deactivated successfully"
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequiresPermission(object = "ROLE", action = "READ")
    @GetMapping("/{roleId}")
    public ApiResponse<RoleResponse> getById(@PathVariable String roleId) {
        return ApiResponse.success(
                roleService.getById(roleId),
                "Role fetched successfully"
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequiresPermission(object = "ROLE", action = "READ")
    @GetMapping
    public ApiResponse<List<RoleResponse>> getAllRoles() {
        return ApiResponse.success(
                roleService.getAllRoles(),
                "Roles fetched successfully"
        );
    }
}
