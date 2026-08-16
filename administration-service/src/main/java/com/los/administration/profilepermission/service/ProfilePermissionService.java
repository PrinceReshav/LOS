package com.los.administration.profilepermission.service;

import com.los.administration.common.exception.ResourceNotFoundException;
import com.los.administration.permission.model.Permission;
import com.los.administration.permission.repository.PermissionRepository;
import com.los.administration.profile.model.Profile;
import com.los.administration.profile.repository.ProfileRepository;
import com.los.administration.profilepermission.dto.ProfilePermissionEntry;
import com.los.administration.profilepermission.dto.ProfilePermissionMatrixResponse;
import com.los.administration.profilepermission.model.ProfilePermission;
import com.los.administration.profilepermission.repository.ProfilePermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Reads and writes the Profile x Permission matrix - the same data
 * {@link com.los.administration.security.service.SecurityPermissionService}
 * checks on every {@code @RequiresPermission} call. Saving here evicts the
 * permission cache so changes take effect immediately rather than waiting
 * for a cache entry to expire.
 */
@Service
@RequiredArgsConstructor
public class ProfilePermissionService {

    private final ProfileRepository profileRepository;
    private final PermissionRepository permissionRepository;
    private final ProfilePermissionRepository profilePermissionRepository;

    @Transactional(readOnly = true)
    public ProfilePermissionMatrixResponse getMatrix(String profileId) {

        Profile profile = profileRepository.findByProfileId(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found: " + profileId));

        List<Permission> allPermissions = permissionRepository.findByActiveTrue();

        Map<String, ProfilePermission> existing = profilePermissionRepository
                .findByProfileId(profileId)
                .stream()
                .collect(java.util.stream.Collectors.toMap(
                        ProfilePermission::getPermissionId,
                        Function.identity()
                ));

        List<ProfilePermissionEntry> entries = allPermissions.stream()
                .map(permission -> {

                    ProfilePermission pp = existing.get(permission.getId());

                    return ProfilePermissionEntry.builder()
                            .permissionId(permission.getId())
                            .permissionCode(permission.getPermissionCode())
                            .permissionName(permission.getPermissionName())
                            .moduleName(permission.getModuleName())
                            .canRead(pp != null && Boolean.TRUE.equals(pp.getCanRead()))
                            .canCreate(pp != null && Boolean.TRUE.equals(pp.getCanCreate()))
                            .canEdit(pp != null && Boolean.TRUE.equals(pp.getCanEdit()))
                            .canDelete(pp != null && Boolean.TRUE.equals(pp.getCanDelete()))
                            .canApprove(pp != null && Boolean.TRUE.equals(pp.getCanApprove()))
                            .build();
                })
                .toList();

        return ProfilePermissionMatrixResponse.builder()
                .profileId(profile.getProfileId())
                .profileName(profile.getProfileName())
                .entries(entries)
                .build();
    }

    /**
     * Bulk-replaces every entry the caller supplied for this profile.
     * Evicts the whole permission cache (rather than trying to compute
     * exact cache keys) since a profile's flags can affect many
     * (object, action) combinations at once.
     */
    @Transactional
    @CacheEvict(value = "permissions", allEntries = true)
    public ProfilePermissionMatrixResponse saveMatrix(String profileId, List<ProfilePermissionEntry> entries) {

        profileRepository.findByProfileId(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found: " + profileId));

        for (ProfilePermissionEntry entry : entries) {

            Permission permission = permissionRepository.findById(entry.getPermissionId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Permission not found: " + entry.getPermissionId()
                    ));

            ProfilePermission pp = profilePermissionRepository
                    .findByProfileIdAndPermissionId(profileId, permission.getId())
                    .orElseGet(() -> ProfilePermission.builder()
                            .profileId(profileId)
                            .permissionId(permission.getId())
                            .build()
                    );

            pp.setCanRead(Boolean.TRUE.equals(entry.getCanRead()));
            pp.setCanCreate(Boolean.TRUE.equals(entry.getCanCreate()));
            pp.setCanEdit(Boolean.TRUE.equals(entry.getCanEdit()));
            pp.setCanDelete(Boolean.TRUE.equals(entry.getCanDelete()));
            pp.setCanApprove(Boolean.TRUE.equals(entry.getCanApprove()));

            profilePermissionRepository.save(pp);
        }

        return getMatrix(profileId);
    }
}
