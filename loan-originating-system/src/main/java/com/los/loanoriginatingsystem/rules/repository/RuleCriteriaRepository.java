package com.los.loanoriginatingsystem.rules.repository;

import com.los.loanoriginatingsystem.rules.entity.RuleCriteria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RuleCriteriaRepository extends JpaRepository<RuleCriteria, String> {

    List<RuleCriteria> findByRuleEngineId(String ruleEngineId);

    List<RuleCriteria> findByRuleEngineIdOrderBySequenceAsc(String ruleEngineId);
}