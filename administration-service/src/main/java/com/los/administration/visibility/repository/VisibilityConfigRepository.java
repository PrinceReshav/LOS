package com.los.administration.visibility.repository;

import com.los.administration.visibility.model.VisibilityConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VisibilityConfigRepository
        extends JpaRepository<VisibilityConfig, Long> {

    Optional<VisibilityConfig> findByEntityName(String entityName);
}