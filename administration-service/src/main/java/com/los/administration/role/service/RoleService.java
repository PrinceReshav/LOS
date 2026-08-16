package com.los.administration.role.service;

import com.los.administration.common.exception.ResourceNotFoundException;
import com.los.administration.role.dto.RoleRequest;
import com.los.administration.role.dto.RoleResponse;
import com.los.administration.role.model.Role;
import com.los.administration.role.model.RoleType;
import com.los.administration.role.repository.RoleRepository;
import com.los.administration.visibility.service.RoleClosureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleClosureService roleClosureService;

    @Transactional
    public RoleResponse createRole(RoleRequest request) {

        if (roleRepository.existsByRoleName(request.getRoleName())) {
            throw new IllegalArgumentException("Role name already exists");
        }

        // FIX: previously only roleName was checked for uniqueness; a
        // duplicate roleId used to fall through to the DB unique
        // constraint and surface as an ugly DataIntegrityViolationException.
        if (roleRepository.existsByRoleId(request.getRoleId())) {
            throw new IllegalArgumentException("Role id already exists");
        }

        Role parent = resolveParent(request.getParentRoleId());

        Role role = Role.builder()
                .roleId(request.getRoleId())
                .roleName(request.getRoleName())
                .roleType(request.getRoleType() != null ? request.getRoleType() : RoleType.STANDARD)
                .description(request.getDescription())
                .parentRole(parent)
                .active(true)
                .systemDefined(false)
                .hierarchyLevel(parent != null ? nullSafeLevel(parent) + 1 : 0)
                .build();

        validateNoCycle(role);

        Role saved = roleRepository.save(role);

        roleClosureService.addRole(saved);

        return toResponse(saved);
    }

    @Transactional
    public RoleResponse updateRole(String roleId, RoleRequest request) {

        Role role = getEntity(roleId);

        if (request.getRoleName() != null) role.setRoleName(request.getRoleName());
        if (request.getRoleType() != null) role.setRoleType(request.getRoleType());
        if (request.getDescription() != null) role.setDescription(request.getDescription());
        if (request.getActive() != null) role.setActive(request.getActive());

        if (request.getParentRoleId() != null) {

            Role parent = resolveParent(request.getParentRoleId());
            role.setParentRole(parent);
            role.setHierarchyLevel(parent != null ? nullSafeLevel(parent) + 1 : 0);
            validateNoCycle(role);

            Role saved = roleRepository.save(role);

            roleClosureService.rebuildForRole(saved);

            return toResponse(saved);
        }

        return toResponse(roleRepository.save(role));
    }

    @Transactional
    public RoleResponse setActive(String roleId, boolean active) {

        Role role = getEntity(roleId);

        if (Boolean.TRUE.equals(role.getSystemDefined()) && !active) {
            throw new IllegalStateException("System-defined roles cannot be deactivated: " + roleId);
        }

        role.setActive(active);

        return toResponse(roleRepository.save(role));
    }

    @Transactional(readOnly = true)
    public Role getEntity(String roleId) {

        return roleRepository.findByRoleId(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleId));
    }

    @Transactional(readOnly = true)
    public RoleResponse getById(String roleId) {
        return toResponse(getEntity(roleId));
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream().map(this::toResponse).toList();
    }

    private Role resolveParent(String parentRoleId) {

        if (parentRoleId == null || parentRoleId.isBlank()) {
            return null;
        }

        return roleRepository.findByRoleId(parentRoleId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent role not found: " + parentRoleId));
    }

    private void validateNoCycle(Role role) {

        Role parent = role.getParentRole();

        while (parent != null) {
            if (parent.getRoleId().equals(role.getRoleId())) {
                throw new IllegalStateException("Role hierarchy cycle detected");
            }
            parent = parent.getParentRole();
        }
    }

    private int nullSafeLevel(Role role) {
        return role.getHierarchyLevel() != null ? role.getHierarchyLevel() : 0;
    }

    private RoleResponse toResponse(Role role) {

        Role parent = role.getParentRole();

        return RoleResponse.builder()
                .roleId(role.getRoleId())
                .roleName(role.getRoleName())
                .roleType(role.getRoleType())
                .description(role.getDescription())
                .parentRoleId(parent != null ? parent.getRoleId() : null)
                .parentRoleName(parent != null ? parent.getRoleName() : null)
                .hierarchyLevel(role.getHierarchyLevel())
                .systemDefined(role.getSystemDefined())
                .active(role.getActive())
                .childCount(role.getChildren() != null ? role.getChildren().size() : 0)
                .build();
    }
}