package com.los.loanoriginatingsystem.rules.service;

import com.los.loanoriginatingsystem.common.exception.ResourceNotFoundException;
import com.los.loanoriginatingsystem.rules.dto.ApprovalMatrixRequest;
import com.los.loanoriginatingsystem.rules.entity.ApprovalMatrix;
import com.los.loanoriginatingsystem.rules.enums.UserRole;
import com.los.loanoriginatingsystem.rules.repository.ApprovalMatrixRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Admin editing of the deviation approval chain (which role approves at
 * which level, and in what order within that level).
 */
@Service
@RequiredArgsConstructor
public class ApprovalMatrixAdminService {

    private final ApprovalMatrixRepository repository;

    @Transactional
    public ApprovalMatrix create(ApprovalMatrixRequest request) {
        validateRole(request.getRequiredRole());

        ApprovalMatrix entity = new ApprovalMatrix();
        entity.setId(UUID.randomUUID().toString());
        entity.setActive(true);
        apply(entity, request);

        return repository.save(entity);
    }

    @Transactional
    public ApprovalMatrix update(String id, ApprovalMatrixRequest request) {
        validateRole(request.getRequiredRole());

        ApprovalMatrix entity = getOrThrow(id);
        apply(entity, request);

        return repository.save(entity);
    }

    @Transactional
    public ApprovalMatrix setActive(String id, boolean active) {
        ApprovalMatrix entity = getOrThrow(id);
        entity.setActive(active);
        return repository.save(entity);
    }

    public List<ApprovalMatrix> getAll() {
        return repository.findAll();
    }

    public List<ApprovalMatrix> getByLevel(Integer level) {
        return repository.findByLevelAndActiveTrueOrderBySequenceAsc(level);
    }

    private void validateRole(String roleCode) {
        UserRole.fromCode(roleCode); // throws IllegalArgumentException if unknown
    }

    private void apply(ApprovalMatrix entity, ApprovalMatrixRequest request) {
        entity.setLevel(request.getLevel());
        entity.setRequiredRole(request.getRequiredRole().trim().toUpperCase());
        entity.setSequence(request.getSequence());
        entity.setDeviationType(request.getDeviationType());
    }

    private ApprovalMatrix getOrThrow(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Approval matrix row not found: " + id));
    }
}
