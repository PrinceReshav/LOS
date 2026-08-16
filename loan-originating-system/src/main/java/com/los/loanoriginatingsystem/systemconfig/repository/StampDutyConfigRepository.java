package com.los.loanoriginatingsystem.systemconfig.repository;

import com.los.loanoriginatingsystem.systemconfig.entity.StampDutyConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StampDutyConfigRepository extends JpaRepository<StampDutyConfig, String> {
    Optional<StampDutyConfig> findByStateCodeAndActiveTrue(String stateCode);
    boolean existsByStateCode(String stateCode);
}
