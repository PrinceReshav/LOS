package com.los.administration.visibility.service;

import com.los.administration.role.model.Role;
import com.los.administration.role.model.RoleType;
import com.los.administration.user.model.User;
import com.los.administration.visibility.model.VisibilityConfig;
import com.los.administration.visibility.model.VisibilityType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VisibilityService {

    private final RoleHierarchyService hierarchyService;

    public boolean canView(User viewer, User target, VisibilityConfig config) {

        // ✅ 1. Self access
        if (viewer.getUserId().equals(target.getUserId())) {
            return true;
        }

        Role viewerRole = viewer.getRole();
        Role targetRole = target.getRole();

        // ✅ 2. ROOT → sees everything
        if (viewerRole.getRoleType() == RoleType.ROOT) {
            return true;
        }

        // ✅ 3. PUBLIC ACCESS
        if (config.getVisibilityType() == VisibilityType.PUBLIC_READ
                || config.getVisibilityType() == VisibilityType.PUBLIC_READ_WRITE) {
            return true;
        }

        // ✅ 4. PRIVATE → Only hierarchy
        if (config.getVisibilityType() == VisibilityType.PRIVATE) {

            // Manager can see subordinate
            if (hierarchyService.isManager(viewerRole, targetRole)) {
                return true;
            }

            // Same role (optional rule)
            if (viewerRole.getId().equals(targetRole.getId())) {
                return true;
            }
        }

        // ❌ Otherwise no access
        return false;
    }
}