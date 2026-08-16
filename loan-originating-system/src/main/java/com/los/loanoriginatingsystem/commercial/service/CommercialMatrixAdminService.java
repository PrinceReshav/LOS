package com.los.loanoriginatingsystem.commercial.service;

import com.los.loanoriginatingsystem.commercial.dto.CommercialMatrixRequest;
import com.los.loanoriginatingsystem.commercial.entity.CommercialMatrix;
import com.los.loanoriginatingsystem.commercial.repository.CommercialMatrixRepository;
import com.los.loanoriginatingsystem.common.exception.ResourceNotFoundException;
import com.los.loanoriginatingsystem.rules.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/** Admin CRUD + activation for Commercial Matrix rows. */
@Service
@RequiredArgsConstructor
public class CommercialMatrixAdminService {

    private final CommercialMatrixRepository repository;

    @Transactional
    public CommercialMatrix create(CommercialMatrixRequest request) {
        validate(request);
        CommercialMatrix entity = new CommercialMatrix();
        entity.setId(UUID.randomUUID().toString());
        entity.setActive(true);
        apply(entity, request);
        return repository.save(entity);
    }

    @Transactional
    public CommercialMatrix update(String id, CommercialMatrixRequest request) {
        validate(request);
        CommercialMatrix entity = getOrThrow(id);
        apply(entity, request);
        return repository.save(entity);
    }

    @Transactional
    public CommercialMatrix setActive(String id, boolean active) {
        CommercialMatrix entity = getOrThrow(id);
        entity.setActive(active);
        return repository.save(entity);
    }

    public List<CommercialMatrix> getAll() {
        return repository.findAll();
    }

    public CommercialMatrix get(String id) {
        return getOrThrow(id);
    }

    private void validate(CommercialMatrixRequest request) {
        if (Boolean.FALSE.equals(request.getAutoApproved())
                && (request.getRequiredRole() == null || request.getRequiredRole().isBlank())) {
            throw new IllegalArgumentException("requiredRole is mandatory unless autoApproved = true");
        }
        if (request.getRequiredRole() != null && !request.getRequiredRole().isBlank()) {
            UserRole.fromCode(request.getRequiredRole()); // throws if unknown role code
        }
        if (request.getMinCreditScore() != null && request.getMaxCreditScore() != null
                && request.getMinCreditScore() > request.getMaxCreditScore()) {
            throw new IllegalArgumentException("minCreditScore cannot be greater than maxCreditScore");
        }
    }

    private void apply(CommercialMatrix entity, CommercialMatrixRequest request) {
        entity.setName(request.getName());
        entity.setScheme(blankToNull(request.getScheme()));
        entity.setLoanType(blankToNull(request.getLoanType()));
        entity.setSecuredLoanCategory(blankToNull(request.getSecuredLoanCategory()));
        entity.setProductCode(blankToNull(request.getProductCode()));
        entity.setMinCreditScore(request.getMinCreditScore());
        entity.setMaxCreditScore(request.getMaxCreditScore());
        entity.setMinLoanAmount(request.getMinLoanAmount());
        entity.setMaxLoanAmount(request.getMaxLoanAmount());
        entity.setMinTotal(request.getMinTotal());
        entity.setMaxTotal(request.getMaxTotal());
        entity.setMinProcessingFee(request.getMinProcessingFee());
        entity.setMaxProcessingFee(request.getMaxProcessingFee());
        entity.setRequiredRole(request.getRequiredRole() != null ? request.getRequiredRole().trim().toUpperCase() : null);
        entity.setAutoApproved(Boolean.TRUE.equals(request.getAutoApproved()));
        entity.setMaxProcessingFeesAllowed(request.getMaxProcessingFeesAllowed());
    }

    private String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim().toUpperCase();
    }

    private CommercialMatrix getOrThrow(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commercial matrix row not found: " + id));
    }
}
