package com.los.losadminservice.designation.repository;

import com.los.losadminservice.designation.model.Designation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DesignationRepository extends JpaRepository<Designation, String> {

    boolean existsByDesignationIdIgnoreCase(String designationId);

    List<Designation> findByDepartmentCodeIgnoreCase(String departmentCode);

    List<Designation> findByActiveTrue();
}
