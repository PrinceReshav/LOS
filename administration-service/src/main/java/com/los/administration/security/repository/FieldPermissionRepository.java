package com.los.administration.security.repository;

import com.los.administration.security.model.SecurityFieldPermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FieldPermissionRepository
        extends JpaRepository<SecurityFieldPermission, Long> {

    List<SecurityFieldPermission> findByProfileIdAndObjectName(
            String profileId,
            String objectName
    );
}