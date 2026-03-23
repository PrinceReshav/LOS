package com.los.loanoriginatingsystem.rules.repository;

import com.los.loanoriginatingsystem.rules.entity.RuleEngine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RuleEngineRepository extends JpaRepository<RuleEngine, String> {

    List<RuleEngine> findByObjectApiNameAndActiveTrue(String objectApiName);
    List<RuleEngine> findByObjectNameAndActiveTrue(String objectName);
    List<RuleEngine> findByIsDefaultTrueAndActiveTrue();

}