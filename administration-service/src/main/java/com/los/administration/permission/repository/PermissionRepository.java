package com.los.administration.permission.repository;

import com.los.administration.permission.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository
        extends JpaRepository<Permission, String> {

    Optional<Permission> findByPermissionCode(String permissionCode);

    boolean existsByPermissionCode(String permissionCode);

    Optional<Permission> findByModuleNameIgnoreCaseAndActiveTrue(String moduleName);

    List<Permission> findByModuleNameIgnoreCase(String moduleName);

    List<Permission> findByActiveTrue();
}
