package com.los.losadminservice.employeeBranchMapping.service;

import com.los.losadminservice.branch.model.Branch;
import com.los.losadminservice.branch.repository.BranchRepository;
import com.los.losadminservice.employee.model.Employee;
import com.los.losadminservice.employee.repository.EmployeeRepository;
import com.los.losadminservice.employee.validator.BranchCapacityValidator;
import com.los.losadminservice.employeeBranchMapping.dto.EmployeeBranchMappingCreateRequest;
import com.los.losadminservice.employeeBranchMapping.dto.EmployeeBranchMappingResponse;
import com.los.losadminservice.employeeBranchMapping.mapper.EmployeeBranchMappingMapper;
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
    private final EmployeeRepository employeeRepository;
    private final BranchCapacityValidator branchCapacityValidator;
    private final BranchRepository branchRepository;

    @Transactional
    public EmployeeBranchMapping create(
            EmployeeBranchMappingCreateRequest req
    ) {

        validator.validateCreate(
                req.getEmployeeId(),
                req.getBranchId()
        );

        Employee employee = employeeRepository
                .findByEmployeeId(req.getEmployeeId())
                .orElseThrow(() ->
                        new RuntimeException("Employee not found"));

        /*
         * BUSINESS RULES
         *
         * RM -> max 1 per branch
         * RO -> max 2 per branch
         */
        branchCapacityValidator.validateBranchCapacity(
                req.getBranchId(),
                employee.getRoleId()
        );

        /*
         * RO and RM can only belong to ONE branch
         */
        validateSingleBranchRoles(employee);


        EmployeeBranchMapping mapping =
                EmployeeBranchMapping.builder()
                        .employeeId(req.getEmployeeId())
                        .branchId(req.getBranchId())
                        .active(true)
                        .assignedAt(LocalDateTime.now())
                        .build();

        return repository.save(mapping);
    }

    @Transactional(readOnly = true)
    public List<EmployeeBranchMapping> getEmployeeMappings(
            String employeeId
    ) {

        return repository.findByEmployeeIdAndActiveTrue(
                employeeId
        );
    }

    @Transactional
    public void deactivate(Long id) {

        EmployeeBranchMapping mapping =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Mapping not found"));

        mapping.setActive(false);
        mapping.setRelievedAt(LocalDateTime.now());

        repository.save(mapping);
    }


    @Transactional(readOnly = true)
    public EmployeeBranchMapping getById(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Mapping not found"
                        ));
    }

    /*
     * RM and RO can only have one branch.
     */
    private void validateSingleBranchRoles(Employee employee) {

        String roleId = employee.getRoleId();

        if (
                "RELATIONSHIP_OFFICER".equals(roleId)
                        || "RELATIONSHIP_MANAGER".equals(roleId)
        ) {

            List<EmployeeBranchMapping> existingMappings =
                    repository.findByEmployeeIdAndActiveTrue(
                            employee.getEmployeeId()
                    );

            if (!existingMappings.isEmpty()) {

                throw new RuntimeException(
                        roleId + " can only belong to one branch"
                );
            }
        }
    }

    /*@Transactional(readOnly = true)
    *public List<EmployeeBranchMappingResponse> getAll() {

        List<EmployeeBranchMapping> mappings =
                repository.findAllByActiveTrue();

        return mappings
                .stream()
                .map(mapping -> {

                    Employee employee =
                            employeeRepository
                                    .findByEmployeeId(
                                            mapping.getEmployeeId()
                                    )
                                    .orElse(null);

                    Branch branch =
                            branchRepository
                                    .findById(
                                            mapping.getBranchId()
                                    )
                                    .orElse(null);

                    return EmployeeBranchMappingMapper.toResponse(
                            mapping,
                            employee,
                            branch
                    );
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public EmployeeBranchMappingResponse getResponseById(
            Long id
    ) {

        EmployeeBranchMapping mapping =
                getById(id);

        Employee employee =
                employeeRepository
                        .findByEmployeeId(
                                mapping.getEmployeeId()
                        )
                        .orElse(null);

        Branch branch =
                branchRepository
                        .findById(
                                mapping.getBranchId()
                        )
                        .orElse(null);

        return EmployeeBranchMappingMapper.toResponse(
                mapping,
                employee,
                branch
        );
    }
    */


    @Transactional(readOnly = true)
    public List<EmployeeBranchMappingResponse> getAll() {

        return repository.findAllResponses();
    }

    @Transactional(readOnly = true)
    public EmployeeBranchMappingResponse getResponseById(
            Long id
    ) {

        return repository.findResponseById(id)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Mapping not found"
                        )
                );
    }
}