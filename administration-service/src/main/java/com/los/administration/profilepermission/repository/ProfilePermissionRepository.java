package com.los.administration.profilepermission.repository;

import com.los.administration.profilepermission.model.ProfilePermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProfilePermissionRepository
        extends JpaRepository<ProfilePermission, Long> {

    List<ProfilePermission> findByProfileId(String profileId);

    Optional<ProfilePermission> findByProfileIdAndPermissionId(String profileId, String permissionId);

    boolean existsByProfileIdAndPermissionId(String profileId, String permissionId);
}
