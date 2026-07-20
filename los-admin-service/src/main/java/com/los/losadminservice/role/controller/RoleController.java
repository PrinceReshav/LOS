package com.los.losadminservice.role.controller;

import com.los.losadminservice.role.dto.RoleRequest;
import com.los.losadminservice.role.dto.RoleResponse;
import com.los.losadminservice.role.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Master-data CRUD for Roles. This - together with /admin/hierarchy-rules -
 * is the configuration surface that lets HR / Admin add a brand-new
 * department's hierarchy (Legal, Operations, Training, ...) without any
 * backend code change: create the Roles, then create HierarchyRules that
 * connect them.
 */
@RestController
@RequestMapping("/admin/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public RoleResponse create(@Valid @RequestBody RoleRequest request) {
        return roleService.create(request);
    }

    @GetMapping("/{roleId}")
    public RoleResponse get(@PathVariable String roleId) {
        return roleService.get(roleId);
    }

    @GetMapping
    public List<RoleResponse> getAll(
            @RequestParam(required = false) String departmentCode,
            @RequestParam(defaultValue = "false") boolean activeOnly
    ) {
        return roleService.getAll(departmentCode, activeOnly);
    }

    @PutMapping("/{roleId}")
    public RoleResponse update(
            @PathVariable String roleId,
            @RequestBody RoleRequest request
    ) {
        return roleService.update(roleId, request);
    }
}
