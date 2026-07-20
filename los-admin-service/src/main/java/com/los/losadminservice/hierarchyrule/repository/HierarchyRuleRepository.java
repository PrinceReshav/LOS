package com.los.losadminservice.hierarchyrule.repository;

import com.los.losadminservice.common.enums.BranchType;
import com.los.losadminservice.hierarchyrule.model.HierarchyRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HierarchyRuleRepository extends JpaRepository<HierarchyRule, Long> {

    /** Rules that apply regardless of the employee's branch type (branch_type IS NULL). */
    List<HierarchyRule> findByDepartmentCodeIgnoreCaseAndFromRoleIdIgnoreCaseAndBranchTypeIsNullAndActiveTrue(
            String departmentCode,
            String fromRoleId
    );

    /** Rules scoped to a specific branch type (e.g. HEAD_OFFICE overrides). */
    List<HierarchyRule> findByDepartmentCodeIgnoreCaseAndFromRoleIdIgnoreCaseAndBranchTypeAndActiveTrue(
            String departmentCode,
            String fromRoleId,
            BranchType branchType
    );

    List<HierarchyRule> findByDepartmentCodeIgnoreCase(String departmentCode);

    boolean existsByDepartmentCodeIgnoreCaseAndFromRoleIdIgnoreCaseAndToRoleIdIgnoreCaseAndBranchType(
            String departmentCode,
            String fromRoleId,
            String toRoleId,
            BranchType branchType
    );
}
