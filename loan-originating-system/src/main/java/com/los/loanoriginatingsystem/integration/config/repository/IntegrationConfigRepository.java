package com.los.loanoriginatingsystem.integration.config.repository;

import com.los.loanoriginatingsystem.integration.config.entity.IntegrationConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IntegrationConfigRepository extends JpaRepository<IntegrationConfig, String> {

    Optional<IntegrationConfig> findByClientName(String clientName);

    Optional<IntegrationConfig> findByClientNameAndActiveTrue(String clientName);

    boolean existsByClientName(String clientName);
}
