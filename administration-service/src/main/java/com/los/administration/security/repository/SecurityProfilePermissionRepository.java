package com.los.administration.security.repository;

import com.los.administration.security.model.SecurityProfilePermission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecurityProfilePermissionRepository
        extends JpaRepository<SecurityProfilePermission, Long> {

    boolean existsByProfileIdAndPermissionIdAndAllowedTrue(
            String profileId,
            Long permissionId
    );
}