package com.los.losadminservice.hierarchyrule.service;

import com.los.losadminservice.common.enums.BranchType;
import com.los.losadminservice.common.exception.BusinessRuleViolationException;
import com.los.losadminservice.common.exception.ResourceNotFoundException;
import com.los.losadminservice.department.service.DepartmentService;
import com.los.losadminservice.hierarchyrule.dto.HierarchyRuleRequest;
import com.los.losadminservice.hierarchyrule.dto.HierarchyRuleResponse;
import com.los.losadminservice.hierarchyrule.model.HierarchyRule;
import com.los.losadminservice.hierarchyrule.repository.HierarchyRuleRepository;
import com.los.losadminservice.role.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * The heart of the configurable hierarchy engine.
 *
 * {@link #getAllowedManagerRoleIds} is the single method the validators call
 * to find out which manager roles a given employee (department + role +
 * branch context) is allowed to report to. Nothing about Sales, Credit, or
 * any other department is hardcoded here - it is all rows in the
 * hierarchy_rules table, editable through this service / its controller.
 */
@Service
@RequiredArgsConstructor
public class HierarchyRuleService {

    private final HierarchyRuleRepository hierarchyRuleRepository;
    private final DepartmentService departmentService;
    private final RoleService roleService;

    @Transactional
    public HierarchyRuleResponse create(HierarchyRuleRequest request) {

        String departmentCode = request.getDepartmentCode().trim().toUpperCase();
        String fromRoleId = request.getFromRoleId().trim().toUpperCase();
        String toRoleId = request.getToRoleId().trim().toUpperCase();

        // fail fast on bad references
        departmentService.getEntity(departmentCode);
        roleService.getEntity(fromRoleId);
        roleService.getEntity(toRoleId);

        if (fromRoleId.equals(toRoleId)) {
            throw new BusinessRuleViolationException(
                    "A role cannot report to itself: " + fromRoleId
            );
        }

        if (duplicateExists(departmentCode, fromRoleId, toRoleId, request.getBranchType())) {
            throw new BusinessRuleViolationException(
                    "This hierarchy rule already exists"
            );
        }

        HierarchyRule rule = HierarchyRule.builder()
                .departmentCode(departmentCode)
                .fromRoleId(fromRoleId)
                .toRoleId(toRoleId)
                .branchType(request.getBranchType())
                .priority(request.getPriority() != null ? request.getPriority() : 0)
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        return toResponse(hierarchyRuleRepository.save(rule));
    }

    @Transactional
    public HierarchyRuleResponse update(Long id, HierarchyRuleRequest request) {

        HierarchyRule rule = getEntity(id);

        if (request.getPriority() != null) rule.setPriority(request.getPriority());
        if (request.getActive() != null) rule.setActive(request.getActive());

        return toResponse(hierarchyRuleRepository.save(rule));
    }

    @Transactional(readOnly = true)
    public HierarchyRule getEntity(Long id) {

        return hierarchyRuleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Hierarchy rule not found: " + id)
                );
    }

    @Transactional(readOnly = true)
    public List<HierarchyRuleResponse> getByDepartment(String departmentCode) {

        return hierarchyRuleRepository
                .findByDepartmentCodeIgnoreCase(departmentCode)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<HierarchyRuleResponse> getAll() {

        return hierarchyRuleRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Resolves the set of manager role ids an employee holding {@code fromRoleId}
     * in {@code departmentCode}, currently sitting at a branch of
     * {@code employeeBranchType}, is allowed to report to.
     *
     * If a branch-specific override exists for the employee's current branch
     * type (e.g. HEAD_OFFICE), it REPLACES the general (any-branch) rule set
     * entirely - this is what expresses "Credit + Head Office employees skip
     * the normal BCM chain and go straight to the Zonal Credit Manager"
     * purely as data.
     */
    @Transactional(readOnly = true)
    public Set<String> getAllowedManagerRoleIds(
            String departmentCode,
            String fromRoleId,
            BranchType employeeBranchType
    ) {

        if (employeeBranchType != null) {

            List<HierarchyRule> overrideRules =
                    hierarchyRuleRepository
                            .findByDepartmentCodeIgnoreCaseAndFromRoleIdIgnoreCaseAndBranchTypeAndActiveTrue(
                                    departmentCode,
                                    fromRoleId,
                                    employeeBranchType
                            );

            if (!overrideRules.isEmpty()) {
                return overrideRules.stream()
                        .map(HierarchyRule::getToRoleId)
                        .collect(java.util.stream.Collectors.toSet());
            }
        }

        List<HierarchyRule> generalRules =
                hierarchyRuleRepository
                        .findByDepartmentCodeIgnoreCaseAndFromRoleIdIgnoreCaseAndBranchTypeIsNullAndActiveTrue(
                                departmentCode,
                                fromRoleId
                        );

        return generalRules.stream()
                .map(HierarchyRule::getToRoleId)
                .collect(java.util.stream.Collectors.toSet());
    }

    private boolean duplicateExists(
            String departmentCode,
            String fromRoleId,
            String toRoleId,
            BranchType branchType
    ) {

        List<HierarchyRule> candidates = branchType == null
                ? hierarchyRuleRepository
                        .findByDepartmentCodeIgnoreCaseAndFromRoleIdIgnoreCaseAndBranchTypeIsNullAndActiveTrue(
                                departmentCode, fromRoleId
                        )
                : hierarchyRuleRepository
                        .findByDepartmentCodeIgnoreCaseAndFromRoleIdIgnoreCaseAndBranchTypeAndActiveTrue(
                                departmentCode, fromRoleId, branchType
                        );

        return candidates.stream().anyMatch(r -> r.getToRoleId().equalsIgnoreCase(toRoleId));
    }

    private HierarchyRuleResponse toResponse(HierarchyRule rule) {

        return HierarchyRuleResponse.builder()
                .id(rule.getId())
                .departmentCode(rule.getDepartmentCode())
                .fromRoleId(rule.getFromRoleId())
                .toRoleId(rule.getToRoleId())
                .branchType(rule.getBranchType())
                .priority(rule.getPriority())
                .active(rule.getActive())
                .build();
    }
}
