package com.los.administration.security.repository;

import com.los.administration.security.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByObjectNameAndAction(String objectName, String action);
}