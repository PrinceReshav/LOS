package com.los.administration.profilepermission.repository;

import com.los.administration.profilepermission.model.ProfilePermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfilePermissionRepository
        extends JpaRepository<ProfilePermission, Long> {

    List<ProfilePermission>
    findByProfileId(String profileId);
}