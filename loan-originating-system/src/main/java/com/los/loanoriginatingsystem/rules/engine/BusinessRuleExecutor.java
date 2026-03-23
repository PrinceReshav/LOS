package com.los.loanoriginatingsystem.rules.engine;

import com.los.loanoriginatingsystem.rules.engine.criteria.CriteriaEvaluator;
import com.los.loanoriginatingsystem.rules.engine.rule.RuleEvaluator;
import com.los.loanoriginatingsystem.rules.entity.RuleCriteria;
import com.los.loanoriginatingsystem.rules.entity.RuleEngine;
import com.los.loanoriginatingsystem.rules.model.BusinessRuleResult;
import com.los.loanoriginatingsystem.rules.model.CriteriaResult;
import com.los.loanoriginatingsystem.rules.repository.RuleCriteriaRepository;
import com.los.loanoriginatingsystem.rules.repository.RuleEngineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class BusinessRuleExecutor {

    private final RuleEngineRepository ruleEngineRepository;
    private final RuleCriteriaRepository criteriaRepository;
    private final CriteriaEvaluator criteriaEvaluator;
    private final RuleEvaluator ruleEvaluator;

    /**
     * 🔥 MAIN ENTRY
     */
    public List<BusinessRuleResult> execute(String objectName, Object entity) {

        List<BusinessRuleResult> results = new ArrayList<>();

        // 🔥 STEP 1: DEFAULT RULES FIRST (Salesforce behavior)
        List<RuleEngine> defaultRules =
                ruleEngineRepository.findByIsDefaultTrueAndActiveTrue();

        results.addAll(evaluateRules(defaultRules, entity));

        // 🔥 STEP 2: IF DEFAULT FAILED → STOP (Salesforce logic)
        if (!results.isEmpty()) {
            return results;
        }

        // 🔥 STEP 3: OBJECT-SPECIFIC RULES
        List<RuleEngine> rules =
                ruleEngineRepository.findByObjectNameAndActiveTrue(objectName);

        results.addAll(evaluateRules(rules, entity));

        return results;
    }

    /**
     * 🔥 CORE RULE LOOP
     */
    private List<BusinessRuleResult> evaluateRules(
            List<RuleEngine> rules,
            Object entity
    ) {

        List<BusinessRuleResult> finalResults = new ArrayList<>();

        for (RuleEngine rule : rules) {

            // 🔥 STEP 1: Load criteria
            List<RuleCriteria> criteriaList =
                    criteriaRepository.findByRuleEngineIdOrderBySequenceAsc(rule.getId());

            if (criteriaList.isEmpty()) continue;

            // 🔥 STEP 2: Evaluate criteria
            List<CriteriaResult> criteriaResults = new ArrayList<>();

            for (RuleCriteria criteria : criteriaList) {

                CriteriaResult result =
                        criteriaEvaluator.evaluate(entity, criteria);

                criteriaResults.add(result);
            }

            // 🔥 STEP 3: Evaluate rule logic
            boolean rulePassed = ruleEvaluator.evaluate(
                    criteriaResults,
                    rule.getOperator(),
                    rule.getCustomLogic()
            );

            // 🔥 STEP 4: If FAILED → collect
            if (!rulePassed) {

                BusinessRuleResult res = new BusinessRuleResult();

                res.setRuleId(rule.getId());
                res.setRuleName(rule.getName());
                res.setDeviationLevel(rule.getDeviationLevel());

                List<String> failedCriteria = new ArrayList<>();

                for (CriteriaResult cr : criteriaResults) {
                    if (!cr.isResult()) {
                        failedCriteria.add("Criteria-" + cr.getSequence());
                    }
                }

                res.setFailedCriteria(failedCriteria);

                finalResults.add(res);
            }
        }

        return finalResults;
    }
}