package com.los.administration.security.service;


import com.los.administration.security.repository.ProfilePermissionRepository;
import com.los.administration.user.model.User;
import com.los.administration.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;


@Service
@RequiredArgsConstructor
public class PermissionService {


    private final ProfilePermissionRepository profilePermissionRepository;
    private final UserRepository userRepository;

    public void checkPermission(String userId, String object, String action) {

        User user = userRepository.findDetailedByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String profileId = user.getProfile().getProfileId();


        boolean allowed =
                hasPermissionCached(
                        profileId,
                        object.toUpperCase(),
                        action.toUpperCase()
                );

        if (!allowed) {
            throw new AccessDeniedException(
                    "ACCESS_DENIED: " + object + " " + action
            );
        }
    }

    @Cacheable(
            value = "permissions",
            key = "#profileId + ':' + #object + ':' + #action"
    )
    public boolean hasPermissionCached(String profileId, String object, String action) {

        return profilePermissionRepository.hasPermission(
                profileId,
                object,
                action
        );
    }
}