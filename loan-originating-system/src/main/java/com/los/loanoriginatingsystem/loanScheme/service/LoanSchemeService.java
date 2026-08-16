package com.los.loanoriginatingsystem.loanScheme.service;

import com.los.loanoriginatingsystem.common.exception.ResourceNotFoundException;
import com.los.loanoriginatingsystem.loanScheme.dto.LoanSchemeRequest;
import com.los.loanoriginatingsystem.loanScheme.entity.LoanSchemeConfig;
import com.los.loanoriginatingsystem.loanScheme.repository.LoanSchemeConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoanSchemeService {

    private final LoanSchemeConfigRepository repository;

    @Transactional
    public LoanSchemeConfig create(LoanSchemeRequest request) {
        if (repository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Loan scheme already exists for code: " + request.getCode());
        }
        LoanSchemeConfig entity = new LoanSchemeConfig();
        entity.setId(UUID.randomUUID().toString());
        entity.setCode(request.getCode());
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        return repository.save(entity);
    }

    @Transactional
    public LoanSchemeConfig update(String id, LoanSchemeRequest request) {
        LoanSchemeConfig entity = getOrThrow(id);
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        return repository.save(entity);
    }

    @Transactional
    public LoanSchemeConfig setActive(String id, boolean active) {
        LoanSchemeConfig entity = getOrThrow(id);
        entity.setActive(active);
        return repository.save(entity);
    }

    public List<LoanSchemeConfig> getAll() {
        return repository.findAll();
    }

    public LoanSchemeConfig getByCode(String code) {
        return repository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Loan scheme not found for code: " + code));
    }

    /** Used by LoanProductService before allowing a product to reference this scheme. */
    public void assertActive(String code) {
        LoanSchemeConfig scheme = repository.findByCode(code).orElse(null);
        if (scheme != null && !Boolean.TRUE.equals(scheme.getActive())) {
            throw new IllegalStateException("Loan scheme is deactivated: " + code);
        }
        // if no matching config row exists yet (legacy enum-only scheme), allow it -
        // this keeps existing seeded products working until the schemes are backfilled.
    }

    private LoanSchemeConfig getOrThrow(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan scheme not found: " + id));
    }
}
