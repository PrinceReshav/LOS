package com.los.losadminservice.employeeBranchMapping.service;

import com.los.losadminservice.common.exception.BusinessRuleViolationException;
import com.los.losadminservice.common.exception.ResourceNotFoundException;
import com.los.losadminservice.employee.model.Employee;
import com.los.losadminservice.employee.validator.BranchCapacityValidator;
import com.los.losadminservice.employeeBranchMapping.dto.EmployeeBranchMappingCreateRequest;
import com.los.losadminservice.employeeBranchMapping.dto.EmployeeBranchMappingResponse;
import com.los.losadminservice.employeeBranchMapping.model.EmployeeBranchMapping;
import com.los.losadminservice.employeeBranchMapping.repository.EmployeeBranchMappingRepository;
import com.los.losadminservice.employeeBranchMapping.validator.EmployeeBranchMappingValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeBranchMappingService {

    private final EmployeeBranchMappingRepository repository;
    private final EmployeeBranchMappingValidator validator;
    private final BranchCapacityValidator branchCapacityValidator;

    @Transactional
    public EmployeeBranchMapping create(EmployeeBranchMappingCreateRequest req) {

        Employee employee = validator.validateCreate(req.getEmployeeId(), req.getBranchId());

        // "1 branch can have only 2 Relationship Managers" (and any other
        // role-specific per-branch cap) - fully data-driven, see Role.maxPerBranch.
        branchCapacityValidator.validateBranchCapacity(req.getBranchId(), employee.getRoleId());

        List<EmployeeBranchMapping> existingActiveMappings =
                repository.findByEmployeeIdAndActiveTrue(req.getEmployeeId());

        boolean makePrimary = Boolean.TRUE.equals(req.getPrimaryBranch())
                || existingActiveMappings.isEmpty();

        if (makePrimary) {
            demoteCurrentPrimary(req.getEmployeeId());
        }

        EmployeeBranchMapping mapping =
                EmployeeBranchMapping.builder()
                        .employeeId(req.getEmployeeId())
                        .branchId(req.getBranchId())
                        .primaryBranch(makePrimary)
                        .active(true)
                        .assignedAt(LocalDateTime.now())
                        .build();

        return repository.save(mapping);
    }

    @Transactional(readOnly = true)
    public List<EmployeeBranchMapping> getEmployeeMappings(String employeeId) {

        return repository.findByEmployeeIdAndActiveTrue(employeeId);
    }

    @Transactional
    public void deactivate(Long id) {

        EmployeeBranchMapping mapping = getById(id);

        boolean wasPrimary = Boolean.TRUE.equals(mapping.getPrimaryBranch());

        mapping.setActive(false);
        mapping.setPrimaryBranch(false);
        mapping.setRelievedAt(LocalDateTime.now());

        repository.save(mapping);

        if (wasPrimary) {
            // Promote another active mapping (if any) to primary so the
            // employee always has exactly one primary branch while they
            // have at least one active mapping.
            repository.findByEmployeeIdAndActiveTrue(mapping.getEmployeeId())
                    .stream()
                    .findFirst()
                    .ifPresent(next -> {
                        next.setPrimaryBranch(true);
                        repository.save(next);
                    });
        }
    }

    /**
     * Explicitly switches which of an employee's active branch mappings is
     * the primary one - used from the Employee Details page as well as the
     * Employee-Branch Mapping screen.
     */
    @Transactional
    public EmployeeBranchMapping setPrimary(Long id) {

        EmployeeBranchMapping mapping = getById(id);

        if (!Boolean.TRUE.equals(mapping.getActive())) {
            throw new BusinessRuleViolationException("Cannot make an inactive mapping primary");
        }

        demoteCurrentPrimary(mapping.getEmployeeId());

        mapping.setPrimaryBranch(true);

        return repository.save(mapping);
    }

    private void demoteCurrentPrimary(String employeeId) {

        repository.findByEmployeeIdAndActiveTrueAndPrimaryBranchTrue(employeeId)
                .ifPresent(current -> {
                    current.setPrimaryBranch(false);
                    repository.save(current);
                });
    }

    @Transactional(readOnly = true)
    public EmployeeBranchMapping getById(Long id) {

        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mapping not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<EmployeeBranchMappingResponse> getAll() {

        return repository.findAllResponses();
    }

    @Transactional(readOnly = true)
    public List<EmployeeBranchMappingResponse> getResponsesForEmployee(String employeeId) {

        return repository.findActiveResponsesByEmployeeId(employeeId);
    }

    @Transactional(readOnly = true)
    public EmployeeBranchMappingResponse getResponseById(Long id) {

        return repository.findResponseById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mapping not found: " + id));
    }
}
