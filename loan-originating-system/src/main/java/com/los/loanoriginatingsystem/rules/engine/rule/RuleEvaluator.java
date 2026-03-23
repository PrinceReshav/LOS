package com.los.loanoriginatingsystem.rules.engine.rule;

import com.los.loanoriginatingsystem.rules.engine.expression.BooleanExpressionEvaluator;
import com.los.loanoriginatingsystem.rules.model.CriteriaResult;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RuleEvaluator {

    private final BooleanExpressionEvaluator expressionEvaluator;

    public RuleEvaluator(BooleanExpressionEvaluator expressionEvaluator) {
        this.expressionEvaluator = expressionEvaluator;
    }

    public boolean evaluate(List<CriteriaResult> results,
                            String operator,
                            String customLogic) {

        // 🔥 CUSTOM LOGIC (highest priority)
        if (customLogic != null && !customLogic.isBlank()) {

            String expression = customLogic;

            for (CriteriaResult r : results) {
                expression = expression.replaceAll(
                        "\\b" + r.getSequence() + "\\b",
                        String.valueOf(r.isResult())
                );
            }

            return expressionEvaluator.evaluate(expression);
        }

        // 🔥 SINGLE CRITERIA
        if (results.size() == 1) {
            return results.get(0).isResult();
        }

        // 🔥 DEFAULT OPERATOR
        if ("AND".equalsIgnoreCase(operator)) {
            return results.stream().allMatch(CriteriaResult::isResult);
        }

        if ("OR".equalsIgnoreCase(operator)) {
            return results.stream().anyMatch(CriteriaResult::isResult);
        }

        throw new IllegalArgumentException("Invalid operator: " + operator);
    }
}