package com.los.administration.role.repository;

import com.los.administration.role.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleId(String roleId);

    Optional<Role> findByRoleName(String roleName);

    boolean existsByRoleName(String roleName);


    boolean existsByRoleId(String roleId);

    @Query("""
    SELECT r FROM Role r
    LEFT JOIN FETCH r.children
    WHERE r.id = :id
    """)
    Role findWithChildren(@Param("id") Long id);

}