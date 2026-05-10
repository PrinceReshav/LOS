package com.los.administration.visibility.service;

import com.los.administration.role.model.Role;
import com.los.administration.user.model.User;
import com.los.administration.user.repository.UserRepository;
import com.los.administration.visibility.model.AccessType;
import com.los.administration.visibility.model.ManualShare;
import com.los.administration.visibility.model.SharingRule;
import com.los.administration.visibility.model.UserVisibility;
import com.los.administration.visibility.repository.ManualShareRepository;
import com.los.administration.visibility.repository.SharingRuleRepository;
import com.los.administration.visibility.repository.UserVisibilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserVisibilityService {

    private final UserRepository userRepository;
    private final UserVisibilityRepository visibilityRepository;
    private final RoleHierarchyService hierarchyService;
    private final SharingRuleRepository sharingRuleRepository;
    private final ManualShareRepository manualShareRepository;

    @Transactional
    public void rebuildVisibilityForUser(User user) {

        String viewerId = user.getUserId();

        // 1. SELF
        save(viewerId, viewerId, AccessType.ROLE_HIERARCHY);

        // 2. ROLE HIERARCHY
        Set<Role> subRoles = hierarchyService.getAllSubordinates(user.getRole());

        List<User> subUsers = userRepository.findAll().stream()
                .filter(u -> subRoles.contains(u.getRole()))
                .toList();

        for (User sub : subUsers) {
            save(viewerId, sub.getUserId(), AccessType.ROLE_HIERARCHY);
        }

        // 🔥 3. SHARING RULES (ROLE → ROLE)
        List<SharingRule> rules =
                sharingRuleRepository.findByFromRoleIdAndActiveTrue(
                        user.getRole().getRoleId()
                );

        for (SharingRule rule : rules) {

            List<User> targetUsers = userRepository.findAll().stream()
                    .filter(u -> u.getRole().getRoleId().equals(rule.getToRoleId()))
                    .toList();

            for (User target : targetUsers) {
                save(viewerId, target.getUserId(), AccessType.RULE);
            }
        }

        // 🔥 4. MANUAL SHARING (USER → USER)
        List<ManualShare> shares =
                manualShareRepository.findByOwnerUserId(viewerId);

        for (ManualShare share : shares) {
            save(viewerId, share.getSharedWithUserId(), AccessType.MANUAL);
        }
    }

    private void save(String viewer, String target, AccessType type) {

        if (!visibilityRepository.existsByViewerUserIdAndTargetUserId(viewer, target)) {

            visibilityRepository.save(
                    UserVisibility.builder()
                            .viewerUserId(viewer)
                            .targetUserId(target)
                            .accessType(type)
                            .build()
            );
        }
    }
}