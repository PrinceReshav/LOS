package com.los.administration.security.repository;

import com.los.administration.security.model.SecurityPermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SecurityPermissionRepository extends JpaRepository<SecurityPermission, Long> {

    Optional<SecurityPermission> findByObjectNameAndAction(String objectName, String action);
}