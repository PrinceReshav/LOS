package com.los.administration.visibility.service;

import com.los.administration.grpc.EmployeeGrpcClient;
import com.los.administration.role.model.Role;
import com.los.administration.role.model.RoleType;
import com.los.administration.role.repository.RoleRepository;
import com.los.administration.user.model.User;
import com.los.administration.user.repository.UserRepository;
import com.los.administration.visibility.dto.*;
import com.los.administration.visibility.model.RecordAccessLevel;
import com.los.administration.visibility.model.RecordShare;
import com.los.administration.visibility.model.VisibilityConfig;
import com.los.administration.visibility.model.VisibilityType;
import com.los.administration.visibility.repository.RecordShareRepository;
import com.los.administration.visibility.repository.VisibilityConfigRepository;
import com.los.grpc.employee.EmployeeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Record-level authorization: "can requestingUserId access this specific
 * record" and "what's the visibility scope for this user, for filtering a
 * list query". This is the piece the old user-to-user VisibilityService /
 * IncrementalVisibilityService never covered - they answer "can employee A
 * see employee B's profile", not "can employee A see loan application X".
 *
 * Deliberately kept to three, composable rules rather than a generic rule
 * engine (the request was explicitly "simpler but featureful"):
 *   1. Ownership - the record's owner always has access.
 *   2. Role hierarchy - a manager (per RoleClosure/RoleHierarchyService)
 *      of the record owner's role always has access, exactly like they
 *      already would for the owner's User profile.
 *   3. Branch match - same-branch colleagues have READ access (not WRITE)
 *      by default, matching how a branch team works day to day.
 * Anything not covered by those three is an explicit exception - RecordShare
 * (rule 4) - rather than a new rule.
 */
@Service
@RequiredArgsConstructor
public class RecordAccessService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RoleHierarchyService roleHierarchyService;
    private final VisibilityConfigRepository visibilityConfigRepository;
    private final RecordShareRepository recordShareRepository;
    private final EmployeeGrpcClient employeeGrpcClient;

    public RecordAccessCheckResponse check(RecordAccessCheckRequest request) {

        User requester = userRepository.findByUserId(request.getRequestingUserId()).orElse(null);
        if (requester == null) {
            return new RecordAccessCheckResponse(false, "Unknown requesting user");
        }

        // 1. Ownership
        if (request.getRequestingUserId().equals(request.getOwnerUserId())) {
            return new RecordAccessCheckResponse(true, "Owner");
        }

        // 2. ROOT sees everything
        if (requester.getRole() != null && requester.getRole().getRoleType() == RoleType.ROOT) {
            return new RecordAccessCheckResponse(true, "Root role");
        }

        // 3. Entity-level PUBLIC visibility config
        VisibilityConfig config = visibilityConfigRepository.findByEntityName(request.getRecordType()).orElse(null);
        if (config != null) {
            boolean publicRead = config.getVisibilityType() == VisibilityType.PUBLIC_READ
                    || config.getVisibilityType() == VisibilityType.PUBLIC_READ_WRITE;
            boolean publicWrite = config.getVisibilityType() == VisibilityType.PUBLIC_READ_WRITE;

            if (request.getRequiredAccess() == RecordAccessLevel.READ && publicRead) {
                return new RecordAccessCheckResponse(true, "Public read visibility for " + request.getRecordType());
            }
            if (request.getRequiredAccess() == RecordAccessLevel.READ_WRITE && publicWrite) {
                return new RecordAccessCheckResponse(true, "Public read/write visibility for " + request.getRecordType());
            }
        }

        // 4. Role-hierarchy: is requester a manager of the owner's role?
        if (request.getOwnerUserId() != null) {
            User owner = userRepository.findByUserId(request.getOwnerUserId()).orElse(null);
            if (owner != null && owner.getRole() != null && requester.getRole() != null
                    && roleHierarchyService.isManager(requester.getRole(), owner.getRole())) {
                return new RecordAccessCheckResponse(true, "Role-hierarchy manager of record owner");
            }
        }

        // 5. Branch match (READ only, by default)
        if (request.getBranchId() != null && !request.getBranchId().isBlank()) {
            String requesterBranchId = resolveBranchId(requester);
            if (request.getBranchId().equals(requesterBranchId)) {
                if (request.getRequiredAccess() == RecordAccessLevel.READ) {
                    return new RecordAccessCheckResponse(true, "Same branch");
                }
            }
        }

        // 6. Explicit RecordShare (user-specific or role-specific, including subordinate roles of the shared-with role)
        List<RecordShare> shares = recordShareRepository
                .findByRecordTypeAndRecordIdAndActiveTrue(request.getRecordType(), request.getRecordId());

        for (RecordShare share : shares) {
            if (!accessLevelSatisfies(share.getAccessLevel(), request.getRequiredAccess())) continue;

            if (request.getRequestingUserId().equals(share.getSharedWithUserId())) {
                return new RecordAccessCheckResponse(true, "Directly shared with user");
            }

            if (share.getSharedWithRoleId() != null && requester.getRole() != null) {
                boolean roleMatches = share.getSharedWithRoleId().equals(requester.getRole().getRoleId());
                boolean subordinateOfSharedRole = roleRepository.findByRoleId(share.getSharedWithRoleId())
                        .map(sharedRole -> roleHierarchyService.getAllSubordinates(sharedRole).contains(requester.getRole()))
                        .orElse(false);

                if (roleMatches || subordinateOfSharedRole) {
                    return new RecordAccessCheckResponse(true, "Shared with requester's role");
                }
            }
        }

        return new RecordAccessCheckResponse(false, "No applicable ownership, hierarchy, branch, or share grant found");
    }

    public RecordAccessScopeResponse scope(RecordAccessScopeRequest request) {

        User requester = userRepository.findByUserId(request.getRequestingUserId())
                .orElseThrow(() -> new IllegalArgumentException("Unknown requesting user: " + request.getRequestingUserId()));

        if (requester.getRole() != null && requester.getRole().getRoleType() == RoleType.ROOT) {
            return new RecordAccessScopeResponse(true, List.of(), List.of(), List.of());
        }

        // Self + everyone whose role is a subordinate of the requester's role.
        Set<String> visibleOwnerUserIds = new java.util.HashSet<>();
        visibleOwnerUserIds.add(requester.getUserId());

        if (requester.getRole() != null) {
            Set<Role> subordinateRoles = roleHierarchyService.getAllSubordinates(requester.getRole());
            Set<String> subordinateRoleIds = subordinateRoles.stream().map(Role::getRoleId).collect(Collectors.toSet());

            userRepository.findAll().stream()
                    .filter(u -> u.getRole() != null && subordinateRoleIds.contains(u.getRole().getRoleId()))
                    .forEach(u -> visibleOwnerUserIds.add(u.getUserId()));
        }

        List<String> visibleBranchIds = new java.util.ArrayList<>();
        String ownBranchId = resolveBranchId(requester);
        if (ownBranchId != null && !ownBranchId.isBlank()) {
            visibleBranchIds.add(ownBranchId);
        }

        Set<String> visibleRecordIds = recordShareRepository
                .findByRecordTypeAndActiveTrue(request.getRecordType())
                .stream()
                .filter(share -> matchesShare(share, requester))
                .map(RecordShare::getRecordId)
                .collect(Collectors.toSet());

        return new RecordAccessScopeResponse(
                false,
                new java.util.ArrayList<>(visibleOwnerUserIds),
                visibleBranchIds,
                new java.util.ArrayList<>(visibleRecordIds)
        );
    }

    private boolean matchesShare(RecordShare share, User requester) {
        if (requester.getUserId().equals(share.getSharedWithUserId())) return true;

        if (share.getSharedWithRoleId() != null && requester.getRole() != null) {
            if (share.getSharedWithRoleId().equals(requester.getRole().getRoleId())) return true;
            return roleRepository.findByRoleId(share.getSharedWithRoleId())
                    .map(sharedRole -> roleHierarchyService.getAllSubordinates(sharedRole).contains(requester.getRole()))
                    .orElse(false);
        }
        return false;
    }

    private boolean accessLevelSatisfies(RecordAccessLevel granted, RecordAccessLevel required) {
        if (required == RecordAccessLevel.READ) return true; // READ_WRITE also satisfies READ
        return granted == RecordAccessLevel.READ_WRITE;
    }

    private String resolveBranchId(User user) {
        if (user.getEmployeeId() == null || user.getEmployeeId().isBlank()) {
            return null;
        }
        try {
            EmployeeResponse employee = employeeGrpcClient.getEmployee(user.getEmployeeId());
            return employee != null ? employee.getBranchId() : null;
        } catch (Exception e) {
            return null; // fail closed on branch scoping if the employee lookup fails - other rules may still grant access
        }
    }
}
