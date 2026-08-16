package com.los.loanoriginatingsystem.loanScheme.repository;

import com.los.loanoriginatingsystem.loanScheme.entity.LoanSchemeConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoanSchemeConfigRepository extends JpaRepository<LoanSchemeConfig, String> {
    Optional<LoanSchemeConfig> findByCode(String code);
    boolean existsByCode(String code);
}
