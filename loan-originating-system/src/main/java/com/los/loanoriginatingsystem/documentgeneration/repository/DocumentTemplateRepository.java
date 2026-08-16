package com.los.loanoriginatingsystem.documentgeneration.repository;

import com.los.loanoriginatingsystem.documentgeneration.entity.DocumentTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocumentTemplateRepository extends JpaRepository<DocumentTemplate, String> {
    Optional<DocumentTemplate> findByCode(String code);
    Optional<DocumentTemplate> findByCodeAndActiveTrue(String code);
    boolean existsByCode(String code);
}
