package com.los.administration.visibility.service;

import com.los.administration.role.model.Role;
import com.los.administration.user.model.User;
import com.los.administration.user.repository.UserRepository;
import com.los.administration.visibility.model.AccessType;
import com.los.administration.visibility.model.UserVisibility;
import com.los.administration.visibility.repository.UserVisibilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Service
@RequiredArgsConstructor
public class IncrementalVisibilityService {

    private final UserRepository userRepository;
    private final UserVisibilityRepository visibilityRepository;

    @Transactional
    public void onUserCreated(User user) {
        addSelf(user);
        addSubordinates(user);
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

    private void addSelf(User user) {
        save(user.getUserId(), user.getUserId(), AccessType.ROLE_HIERARCHY);
    }

    private void addSubordinates(User user) {

        List<User> subUsers =
                userRepository.findSubordinateUsers(
                        user.getRole().getRoleId()
                );

        for (User sub : subUsers) {
            save(user.getUserId(), sub.getUserId(), AccessType.ROLE_HIERARCHY);
        }
    }

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