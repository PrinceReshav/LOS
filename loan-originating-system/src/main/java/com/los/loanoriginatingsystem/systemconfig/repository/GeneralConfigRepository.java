package com.los.loanoriginatingsystem.systemconfig.repository;

import com.los.loanoriginatingsystem.systemconfig.entity.GeneralConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GeneralConfigRepository extends JpaRepository<GeneralConfig, String> {
    Optional<GeneralConfig> findByConfigKeyAndActiveTrue(String configKey);
    boolean existsByConfigKey(String configKey);
}
