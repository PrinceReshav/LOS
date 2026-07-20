package com.los.losadminservice.employee.validator;

import com.los.losadminservice.branch.model.Branch;
import com.los.losadminservice.branch.repository.BranchRepository;
import com.los.losadminservice.common.enums.BranchType;
import com.los.losadminservice.employeeBranchMapping.model.EmployeeBranchMapping;
import com.los.losadminservice.employeeBranchMapping.repository.EmployeeBranchMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Determines the branch-type context to use when resolving hierarchy rules
 * for an employee.
 *
 * If the employee is actively mapped to a Head Office branch (whether or
 * not it is their primary branch), HEAD_OFFICE context wins - this is what
 * makes the Credit "Head Office pre-sanctioning team reports straight to
 * the Zonal Credit Manager" rule apply correctly even for an employee who
 * might also be mapped elsewhere. Otherwise, the primary branch's type is
 * used. An employee not yet mapped to any branch resolves to null (no
 * branch-specific override applies - only the branch-agnostic rules do).
 */
@Component
@RequiredArgsConstructor
public class EmployeeBranchContextResolver {

    private final EmployeeBranchMappingRepository mappingRepository;
    private final BranchRepository branchRepository;

    public BranchType resolve(String employeeId) {

        List<EmployeeBranchMapping> mappings =
                mappingRepository.findByEmployeeIdAndActiveTrue(employeeId);

        if (mappings.isEmpty()) {
            return null;
        }

        boolean mappedToHeadOffice = mappings.stream()
                .map(m -> branchRepository.findById(m.getBranchId()).orElse(null))
                .filter(java.util.Objects::nonNull)
                .anyMatch(b -> b.getBranchType() == BranchType.HEAD_OFFICE);

        if (mappedToHeadOffice) {
            return BranchType.HEAD_OFFICE;
        }

        return mappings.stream()
                .filter(m -> Boolean.TRUE.equals(m.getPrimaryBranch()))
                .findFirst()
                .or(() -> mappings.stream().findFirst())
                .map(m -> branchRepository.findById(m.getBranchId()).orElse(null))
                .map(Branch::getBranchType)
                .orElse(BranchType.NORMAL);
    }
}
