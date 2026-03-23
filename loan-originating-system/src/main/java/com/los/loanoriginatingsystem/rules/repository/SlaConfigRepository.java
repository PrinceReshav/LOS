package com.los.loanoriginatingsystem.rules.repository;

import com.los.loanoriginatingsystem.rules.entity.Deviation;
import com.los.loanoriginatingsystem.rules.entity.SlaConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SlaConfigRepository extends JpaRepository<SlaConfig, String> {

    List<SlaConfig> findByActiveTrue();
    List<Deviation> findByStatus(String status);
}