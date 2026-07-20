package com.los.losadminservice.designation.service;

import com.los.losadminservice.common.exception.BusinessRuleViolationException;
import com.los.losadminservice.common.exception.ResourceNotFoundException;
import com.los.losadminservice.department.service.DepartmentService;
import com.los.losadminservice.designation.dto.DesignationRequest;
import com.los.losadminservice.designation.dto.DesignationResponse;
import com.los.losadminservice.designation.model.Designation;
import com.los.losadminservice.designation.repository.DesignationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DesignationService {

    private final DesignationRepository designationRepository;
    private final DepartmentService departmentService;

    @Transactional
    public DesignationResponse create(DesignationRequest request) {

        String id = request.getDesignationId().trim().toUpperCase();

        if (designationRepository.existsByDesignationIdIgnoreCase(id)) {
            throw new BusinessRuleViolationException("Designation already exists: " + id);
        }

        String departmentCode = normalizeDept(request.getDepartmentCode());

        Designation designation = Designation.builder()
                .designationId(id)
                .name(request.getName())
                .departmentCode(departmentCode)
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        return toResponse(designationRepository.save(designation));
    }

    @Transactional
    public DesignationResponse update(String designationId, DesignationRequest request) {

        Designation designation = getEntity(designationId);

        if (request.getName() != null) designation.setName(request.getName());

        if (request.getDepartmentCode() != null) {
            designation.setDepartmentCode(normalizeDept(request.getDepartmentCode()));
        }

        if (request.getActive() != null) designation.setActive(request.getActive());

        return toResponse(designationRepository.save(designation));
    }

    @Transactional(readOnly = true)
    public Designation getEntity(String designationId) {

        return designationRepository.findById(designationId.toUpperCase())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Designation not found: " + designationId)
                );
    }

    @Transactional(readOnly = true)
    public DesignationResponse get(String designationId) {
        return toResponse(getEntity(designationId));
    }

    @Transactional(readOnly = true)
    public List<DesignationResponse> getAll(String departmentCode, boolean activeOnly) {

        List<Designation> designations;

        if (departmentCode != null && !departmentCode.isBlank()) {
            designations = designationRepository.findByDepartmentCodeIgnoreCase(departmentCode);
        } else if (activeOnly) {
            designations = designationRepository.findByActiveTrue();
        } else {
            designations = designationRepository.findAll();
        }

        if (activeOnly) {
            designations = designations.stream()
                    .filter(d -> Boolean.TRUE.equals(d.getActive()))
                    .toList();
        }

        return designations.stream().map(this::toResponse).toList();
    }

    private String normalizeDept(String departmentCode) {

        if (departmentCode == null || departmentCode.isBlank()) {
            return null;
        }

        String code = departmentCode.trim().toUpperCase();
        departmentService.getEntity(code);
        return code;
    }

    private DesignationResponse toResponse(Designation designation) {

        return DesignationResponse.builder()
                .designationId(designation.getDesignationId())
                .name(designation.getName())
                .departmentCode(designation.getDepartmentCode())
                .active(designation.getActive())
                .build();
    }
}
