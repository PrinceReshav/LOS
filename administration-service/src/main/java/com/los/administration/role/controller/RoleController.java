package com.los.administration.role.controller;

import com.los.administration.common.dto.ApiResponse;
import com.los.administration.role.model.Role;
import com.los.administration.role.service.RoleService;
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
    @PostMapping
    public ApiResponse<Role> createRole(@Valid @RequestBody Role role) {
        Role saved = roleService.createRole(role);
        return ApiResponse.success(saved, "Role created successfully");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ApiResponse<List<Role>> getAllRoles() {
        return ApiResponse.success(
                roleService.getAllRoles(),
                "Roles fetched successfully"
        );
    }
}