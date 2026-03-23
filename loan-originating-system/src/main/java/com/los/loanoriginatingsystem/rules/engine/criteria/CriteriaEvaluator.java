package com.los.loanoriginatingsystem.rules.engine.criteria;

import com.los.loanoriginatingsystem.rules.engine.eval.ExpressionEvaluator;
import com.los.loanoriginatingsystem.rules.model.CriteriaResult;
import com.los.loanoriginatingsystem.rules.util.FieldExtractor;
import com.los.loanoriginatingsystem.rules.entity.RuleCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CriteriaEvaluator {

    private final FieldExtractor fieldExtractor;
    private final ExpressionEvaluator expressionEvaluator;

    public CriteriaResult evaluate(Object entity, RuleCriteria criteria) {

        Object leftValue = fieldExtractor.extract(entity, criteria.getFieldName());

        Object rightValue;

        // 🔥 FIELD vs FIELD
        if (Boolean.TRUE.equals(criteria.getFieldCompare())) {
            rightValue = fieldExtractor.extract(entity, criteria.getValue());
        }
        // 🔥 FIELD vs CONSTANT
        else {
            rightValue = criteria.getValue();
        }

        boolean result = expressionEvaluator.evaluate(
                leftValue,
                rightValue,
                criteria.getOperator()
        );

        return new CriteriaResult(criteria.getSequence(), result);
    }
}