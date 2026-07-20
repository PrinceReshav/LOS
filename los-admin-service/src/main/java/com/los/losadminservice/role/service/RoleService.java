package com.los.losadminservice.role.service;

import com.los.losadminservice.common.exception.BusinessRuleViolationException;
import com.los.losadminservice.common.exception.ResourceNotFoundException;
import com.los.losadminservice.department.service.DepartmentService;
import com.los.losadminservice.role.dto.RoleRequest;
import com.los.losadminservice.role.dto.RoleResponse;
import com.los.losadminservice.role.model.Role;
import com.los.losadminservice.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final DepartmentService departmentService;

    @Transactional
    public RoleResponse create(RoleRequest request) {

        String roleId = request.getRoleId().trim().toUpperCase();
        String departmentCode = request.getDepartmentCode().trim().toUpperCase();

        if (roleRepository.existsByRoleIdIgnoreCase(roleId)) {
            throw new BusinessRuleViolationException("Role already exists: " + roleId);
        }

        // fail fast: department must already exist
        departmentService.getEntity(departmentCode);

        Role role = Role.builder()
                .roleId(roleId)
                .roleName(request.getRoleName())
                .departmentCode(departmentCode)
                .isTopLevel(Boolean.TRUE.equals(request.getIsTopLevel()))
                .singleBranchOnly(Boolean.TRUE.equals(request.getSingleBranchOnly()))
                .requiresManagerBranchAlign(Boolean.TRUE.equals(request.getRequiresManagerBranchAlign()))
                .maxPerBranch(request.getMaxPerBranch())
                .maxDirectReports(request.getMaxDirectReports())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        return toResponse(roleRepository.save(role));
    }

    @Transactional
    public RoleResponse update(String roleId, RoleRequest request) {

        Role role = getEntity(roleId);

        if (request.getRoleName() != null) role.setRoleName(request.getRoleName());

        if (request.getDepartmentCode() != null) {
            String departmentCode = request.getDepartmentCode().trim().toUpperCase();
            departmentService.getEntity(departmentCode);
            role.setDepartmentCode(departmentCode);
        }

        if (request.getIsTopLevel() != null) role.setIsTopLevel(request.getIsTopLevel());
        if (request.getSingleBranchOnly() != null) role.setSingleBranchOnly(request.getSingleBranchOnly());
        if (request.getRequiresManagerBranchAlign() != null)
            role.setRequiresManagerBranchAlign(request.getRequiresManagerBranchAlign());
        if (request.getMaxPerBranch() != null) role.setMaxPerBranch(request.getMaxPerBranch());
        if (request.getMaxDirectReports() != null) role.setMaxDirectReports(request.getMaxDirectReports());
        if (request.getActive() != null) role.setActive(request.getActive());

        return toResponse(roleRepository.save(role));
    }

    @Transactional(readOnly = true)
    public Role getEntity(String roleId) {

        return roleRepository.findById(roleId.toUpperCase())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Role not found: " + roleId)
                );
    }

    @Transactional(readOnly = true)
    public RoleResponse get(String roleId) {
        return toResponse(getEntity(roleId));
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> getAll(String departmentCode, boolean activeOnly) {

        List<Role> roles;

        if (departmentCode != null && !departmentCode.isBlank()) {
            roles = roleRepository.findByDepartmentCodeIgnoreCase(departmentCode);
        } else if (activeOnly) {
            roles = roleRepository.findByActiveTrue();
        } else {
            roles = roleRepository.findAll();
        }

        if (activeOnly) {
            roles = roles.stream().filter(r -> Boolean.TRUE.equals(r.getActive())).toList();
        }

        return roles.stream().map(this::toResponse).toList();
    }

    private RoleResponse toResponse(Role role) {

        return RoleResponse.builder()
                .roleId(role.getRoleId())
                .roleName(role.getRoleName())
                .departmentCode(role.getDepartmentCode())
                .isTopLevel(role.getIsTopLevel())
                .singleBranchOnly(role.getSingleBranchOnly())
                .requiresManagerBranchAlign(role.getRequiresManagerBranchAlign())
                .maxPerBranch(role.getMaxPerBranch())
                .maxDirectReports(role.getMaxDirectReports())
                .active(role.getActive())
                .build();
    }
}
