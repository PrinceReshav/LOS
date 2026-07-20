package com.los.losadminservice.role.repository;

import com.los.losadminservice.role.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, String> {

    boolean existsByRoleIdIgnoreCase(String roleId);

    List<Role> findByDepartmentCodeIgnoreCase(String departmentCode);

    List<Role> findByActiveTrue();
}
