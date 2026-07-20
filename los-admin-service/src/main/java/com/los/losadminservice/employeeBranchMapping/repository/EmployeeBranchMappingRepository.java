package com.los.losadminservice.employeeBranchMapping.repository;

import com.los.losadminservice.employeeBranchMapping.dto.EmployeeBranchMappingResponse;
import com.los.losadminservice.employeeBranchMapping.model.EmployeeBranchMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EmployeeBranchMappingRepository
        extends JpaRepository<EmployeeBranchMapping, Long> {

    List<EmployeeBranchMapping> findByEmployeeIdAndActiveTrue(
            String employeeId
    );

    Optional<EmployeeBranchMapping> findByEmployeeIdAndActiveTrueAndPrimaryBranchTrue(
            String employeeId
    );

    List<EmployeeBranchMapping> findByBranchIdAndActiveTrue(
            String branchId
    );

    boolean existsByEmployeeIdAndBranchId(
            String employeeId,
            String branchId
    );

    boolean existsByEmployeeIdAndBranchIdAndActiveTrue(
            String employeeId,
            String branchId
    );

    @Query("""
        select new com.los.losadminservice.employeeBranchMapping.dto.EmployeeBranchMappingResponse(
            m.id,
            e.employeeId,
            e.fullName,
            b.id,
            b.branchName,
            m.primaryBranch,
            m.active,
            m.assignedAt,
            m.relievedAt
        )
        from EmployeeBranchMapping m
        join Employee e
            on e.employeeId = m.employeeId
        join Branch b
            on b.id = m.branchId
        where m.active = true
    """)
    List<EmployeeBranchMappingResponse> findAllResponses();

    @Query("""
        select new com.los.losadminservice.employeeBranchMapping.dto.EmployeeBranchMappingResponse(
            m.id,
            e.employeeId,
            e.fullName,
            b.id,
            b.branchName,
            m.primaryBranch,
            m.active,
            m.assignedAt,
            m.relievedAt
        )
        from EmployeeBranchMapping m
        join Employee e
            on e.employeeId = m.employeeId
        join Branch b
            on b.id = m.branchId
        where m.employeeId = :employeeId
        and m.active = true
    """)
    List<EmployeeBranchMappingResponse> findActiveResponsesByEmployeeId(String employeeId);

    @Query("""
        select new com.los.losadminservice.employeeBranchMapping.dto.EmployeeBranchMappingResponse(
            m.id,
            e.employeeId,
            e.fullName,
            b.id,
            b.branchName,
            m.primaryBranch,
            m.active,
            m.assignedAt,
            m.relievedAt
        )
        from EmployeeBranchMapping m
        join Employee e
            on e.employeeId = m.employeeId
        join Branch b
            on b.id = m.branchId
        where m.id = :id
    """)
    Optional<EmployeeBranchMappingResponse> findResponseById(
            Long id
    );
}
