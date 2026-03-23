package com.los.loanoriginatingsystem.rules.engine.eval;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ExpressionEvaluator {

    public boolean evaluate(Object left, Object right, String operatorStr) {

        Operator operator = Operator.from(operatorStr);

        // Null safety
        if (left == null || right == null) {
            return handleNull(left, right, operator);
        }

        // Numeric check
        if (isNumber(left) && isNumber(right)) {
            return evaluateNumeric(toDecimal(left), toDecimal(right), operator);
        }

        // Default → String comparison
        return evaluateString(String.valueOf(left), String.valueOf(right), operator);
    }

    // =========================
    // NUMERIC
    // =========================

    private boolean evaluateNumeric(BigDecimal left, BigDecimal right, Operator op) {
        switch (op) {
            case GT: return left.compareTo(right) > 0;
            case LT: return left.compareTo(right) < 0;
            case GTE: return left.compareTo(right) >= 0;
            case LTE: return left.compareTo(right) <= 0;
            case EQ: return left.compareTo(right) == 0;
            case NE: return left.compareTo(right) != 0;
            default:
                throw new IllegalArgumentException("Invalid numeric operator: " + op);
        }
    }

    // =========================
    // STRING
    // =========================

    private boolean evaluateString(String left, String right, Operator op) {
        switch (op) {
            case EQ: return left.equalsIgnoreCase(right);
            case NE: return !left.equalsIgnoreCase(right);
            case CONTAINS: return left.contains(right);
            default:
                throw new IllegalArgumentException("Invalid string operator: " + op);
        }
    }

    // =========================
    // NULL HANDLING
    // =========================

    private boolean handleNull(Object left, Object right, Operator op) {
        if (op == Operator.EQ) {
            return left == null && right == null;
        }
        if (op == Operator.NE) {
            return !(left == null && right == null);
        }
        return false;
    }

    // =========================
    // TYPE HELPERS
    // =========================

    private boolean isNumber(Object obj) {
        try {
            new BigDecimal(obj.toString());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private BigDecimal toDecimal(Object obj) {
        return new BigDecimal(obj.toString());
    }
}