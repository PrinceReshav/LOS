package com.los.losadminservice.employee.repository;

import com.los.losadminservice.employee.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("""
SELECT e FROM Employee e
WHERE e.managerEmployeeId IS NOT NULL
""")
    List<Employee> findAllWithManagers();

    boolean existsByEmployeeId(String employeeId);

    boolean existsByUserId(String userId);

    Optional<Employee> findByEmployeeId(String employeeId);

    List<Employee> findByEmployeeIdContainingIgnoreCase(String employeeId);

    Optional<Employee> findByUserId(String userId);

    long countByBranchIdAndRoleId(String branchId,String roleId);

}