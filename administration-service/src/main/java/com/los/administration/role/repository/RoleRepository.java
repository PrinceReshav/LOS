package com.los.administration.role.repository;

import com.los.administration.role.model.Role;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleId(String roleId);

    Optional<Role> findByRoleName(String roleName);

    boolean existsByRoleName(String roleName);

}