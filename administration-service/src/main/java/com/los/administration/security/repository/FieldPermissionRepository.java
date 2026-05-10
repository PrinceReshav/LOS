package com.los.administration.security.repository;

import com.los.administration.security.model.FieldPermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FieldPermissionRepository
        extends JpaRepository<FieldPermission, Long> {

    List<FieldPermission> findByProfileIdAndObjectName(
            String profileId,
            String objectName
    );
}