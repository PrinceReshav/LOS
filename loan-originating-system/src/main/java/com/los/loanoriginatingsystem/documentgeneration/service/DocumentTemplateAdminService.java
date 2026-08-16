package com.los.loanoriginatingsystem.documentgeneration.service;

import com.los.loanoriginatingsystem.common.exception.ResourceNotFoundException;
import com.los.loanoriginatingsystem.documentgeneration.dto.DocumentTemplateRequest;
import com.los.loanoriginatingsystem.documentgeneration.entity.DocumentTemplate;
import com.los.loanoriginatingsystem.documentgeneration.repository.DocumentTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentTemplateAdminService {

    private final DocumentTemplateRepository repository;

    @Transactional
    public DocumentTemplate create(DocumentTemplateRequest request) {
        if (repository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("A document template already exists for code: " + request.getCode());
        }
        DocumentTemplate entity = new DocumentTemplate();
        entity.setId(UUID.randomUUID().toString());
        entity.setActive(true);
        apply(entity, request);
        return repository.save(entity);
    }

    @Transactional
    public DocumentTemplate update(String id, DocumentTemplateRequest request) {
        DocumentTemplate entity = getOrThrow(id);
        apply(entity, request);
        return repository.save(entity); // @PreUpdate bumps `version`
    }

    @Transactional
    public DocumentTemplate setActive(String id, boolean active) {
        DocumentTemplate entity = getOrThrow(id);
        entity.setActive(active);
        return repository.save(entity);
    }

    public List<DocumentTemplate> getAll() {
        return repository.findAll();
    }

    public DocumentTemplate get(String id) {
        return getOrThrow(id);
    }

    private void apply(DocumentTemplate entity, DocumentTemplateRequest request) {
        entity.setCode(request.getCode().trim().toUpperCase());
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setApplicableStage(request.getApplicableStage());
        entity.setHtmlContent(request.getHtmlContent());
    }

    private DocumentTemplate getOrThrow(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document template not found: " + id));
    }
}
