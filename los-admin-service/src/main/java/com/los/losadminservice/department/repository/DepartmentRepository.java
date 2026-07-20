package com.los.losadminservice.department.repository;

import com.los.losadminservice.department.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, String> {

    boolean existsByCodeIgnoreCase(String code);

    List<Department> findByActiveTrue();
}
