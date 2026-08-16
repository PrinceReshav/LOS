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

    List<Employee>
    findByEmployeeIdContainingIgnoreCaseOrFullNameContainingIgnoreCase(
            String employeeId,
            String fullName
    );

    List<Employee> findByManagerEmployeeId(
            String managerEmployeeId
    );

    Optional<Employee> findByUserId(String userId);

    List<Employee> findByApproverRoleCodeAndActiveTrue(String approverRoleCode);

    List<Employee> findByEmployeeIdIn(List<String> employeeIds);

}