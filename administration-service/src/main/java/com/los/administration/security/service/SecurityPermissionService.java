package com.los.administration.security.service;

import com.los.administration.permission.model.Permission;
import com.los.administration.permission.repository.PermissionRepository;
import com.los.administration.profilepermission.model.ProfilePermission;
import com.los.administration.profilepermission.repository.ProfilePermissionRepository;
import com.los.administration.role.model.RoleType;
import com.los.administration.user.model.User;
import com.los.administration.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Enforces the object-level permission matrix (Permission x Profile ->
 * Read/Create/Edit/Delete/Approve) checked by every {@code @RequiresPermission}
 * annotated method via {@link com.los.administration.security.aspect.PermissionAspect}.
 *
 * IMPORTANT HISTORY: this previously always returned {@code true} regardless
 * of the profile's actual permissions - every {@code @RequiresPermission}
 * check in the system was a silent no-op. This rewrite makes the check real
 * and fails CLOSED: if a module has no configured Permission row, or the
 * profile has no ProfilePermission row for it, or the specific action flag
 * isn't granted, access is denied.
 *
 * ROOT-type roles (e.g. the ADMIN role seeded as RoleType.ROOT) bypass the
 * matrix entirely, consistent with how UserService already treats ROOT/ADMIN
 * as having full visibility - this is a deliberate, single, auditable
 * bypass rather than a hidden always-true stub.
 */
@Service("securityPermissionService")
@RequiredArgsConstructor
public class SecurityPermissionService {

    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final ProfilePermissionRepository profilePermissionRepository;

    public void checkPermission(String userId, String object, String action) {

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new AccessDeniedException("ACCESS_DENIED: unknown user"));

        if (user.getRole() != null
                && (user.getRole().getRoleType() == RoleType.ROOT
                        || "ADMIN".equalsIgnoreCase(user.getRole().getRoleName()))) {
            return;
        }

        if (user.getProfile() == null) {
            throw new AccessDeniedException("ACCESS_DENIED: user has no profile assigned");
        }

        String profileId = user.getProfile().getProfileId();

        boolean allowed = hasPermissionCached(
                profileId,
                object.toUpperCase(),
                action.toUpperCase()
        );

        if (!allowed) {
            throw new AccessDeniedException("ACCESS_DENIED: " + object + " " + action);
        }
    }

    @Cacheable(
            value = "permissions",
            key = "#profileId + ':' + #object + ':' + #action"
    )
    public boolean hasPermissionCached(String profileId, String object, String action) {

        Optional<Permission> permission =
                permissionRepository.findByModuleNameIgnoreCaseAndActiveTrue(object);

        if (permission.isEmpty()) {
            // No permission is configured for this module at all -> deny.
            // (Fail closed: an unconfigured module must never mean "allowed".)
            return false;
        }

        Optional<ProfilePermission> profilePermission =
                profilePermissionRepository.findByProfileIdAndPermissionId(
                        profileId,
                        permission.get().getId()
                );

        if (profilePermission.isEmpty()) {
            return false;
        }

        return resolveFlag(profilePermission.get(), action);
    }

    private boolean resolveFlag(ProfilePermission pp, String action) {

        return switch (action) {
            case "CREATE" -> Boolean.TRUE.equals(pp.getCanCreate());
            case "READ", "VIEW" -> Boolean.TRUE.equals(pp.getCanRead());
            case "UPDATE", "EDIT" -> Boolean.TRUE.equals(pp.getCanEdit());
            case "DELETE" -> Boolean.TRUE.equals(pp.getCanDelete());
            case "APPROVE" -> Boolean.TRUE.equals(pp.getCanApprove());
            default -> false;
        };
    }
}
