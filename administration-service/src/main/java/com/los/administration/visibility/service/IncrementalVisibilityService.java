package com.los.administration.visibility.service;

import com.los.administration.role.model.Role;
import com.los.administration.user.model.User;
import com.los.administration.user.repository.UserRepository;
import com.los.administration.visibility.model.AccessType;
import com.los.administration.visibility.model.UserVisibility;
import com.los.administration.visibility.repository.UserVisibilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class IncrementalVisibilityService {

    private final UserRepository userRepository;
    private final UserVisibilityRepository visibilityRepository;

    // FIX: previously this class computed visibility purely from
    // role_closure (self + subordinates) and never looked at
    // ManualShare or SharingRule at all. That meant
    // POST /admin/manual-share and POST /admin/sharing-rules persisted
    // rows and then called onUserCreated(...), which silently ignored
    // those very tables - the features looked wired up end-to-end but
    // had no actual effect on who could see whom.
    //
    // UserVisibilityService.rebuildVisibilityForUser already computes
    // self + role-hierarchy subordinates + sharing rules + manual shares
    // correctly, so we delegate to it here instead of duplicating (and
    // under-implementing) that logic.
    private final UserVisibilityService userVisibilityService;

    @Transactional
    public void onUserCreated(User user) {
        userVisibilityService.rebuildVisibilityForUser(user);
        updateManagers(user);
    }

    @Transactional
    public void onUserActivated(User user) {
        // rebuild visibility for this user
        onUserCreated(user);
    }

    @Transactional
    public void onUserDeactivated(User user) {

        visibilityRepository.deleteByViewerUserId(user.getUserId());
        visibilityRepository.deleteByTargetUserId(user.getUserId());
    }


    @Transactional
    public void onRoleChanged(User user, Role oldRole) {

        // remove old visibility
        visibilityRepository.deleteByViewerUserId(user.getUserId());
        visibilityRepository.deleteByTargetUserId(user.getUserId());

        // rebuild minimal
        onUserCreated(user);
    }

    /**
     * Backfills the "manager can see this new/updated user" direction.
     * rebuildVisibilityForUser only computes visibility rows where
     * {@code user} is the viewer; managers above them also need a row
     * where the manager is the viewer and {@code user} is the target.
     */
    private void updateManagers(User user) {

        List<User> managers =
                userRepository.findManagers(
                        user.getRole().getRoleId()
                );

        for (User manager : managers) {

            if (
                    manager.getUserId()
                            .equals(user.getUserId())
            ) {
                continue;
            }

            save(
                    manager.getUserId(),
                    user.getUserId(),
                    AccessType.ROLE_HIERARCHY
            );
        }
    }

    private void save(String viewer, String target, AccessType type) {

        boolean exists =
                visibilityRepository
                        .existsByViewerUserIdAndTargetUserId(
                                viewer,
                                target
                        );

        if (!exists) {

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