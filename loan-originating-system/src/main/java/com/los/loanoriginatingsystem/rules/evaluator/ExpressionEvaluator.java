package com.los.loanoriginatingsystem.rules.evaluator;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class ExpressionEvaluator {

    public BigDecimal mathEval(List<Object> stack) {

        List<Object> work = new ArrayList<>(stack);

        while (work.size() > 1) {

            Object expr0 = work.get(0);
            Object expr1 = work.get(1);
            Object expr2 = work.size() > 2 ? work.get(2) : null;

            if (!(expr0 instanceof Number) || !(expr1 instanceof Number) || !(expr2 instanceof String)) {
                throw new RuntimeException("Invalid RPN expression: " + stack);
            }

            BigDecimal left = new BigDecimal(expr0.toString());
            BigDecimal right = new BigDecimal(expr1.toString());
            String operator = (String) expr2;

            BigDecimal res;

            switch (operator) {
                case "+" -> res = left.add(right);
                case "-" -> res = left.subtract(right);
                case "*" -> res = left.multiply(right);
                case "/" -> res = left.divide(right);
                case ">=" -> res = left.compareTo(right) >= 0 ? BigDecimal.ONE : BigDecimal.ZERO;
                case "<=" -> res = left.compareTo(right) <= 0 ? BigDecimal.ONE : BigDecimal.ZERO;
                case ">" -> res = left.compareTo(right) > 0 ? BigDecimal.ONE : BigDecimal.ZERO;
                case "<" -> res = left.compareTo(right) < 0 ? BigDecimal.ONE : BigDecimal.ZERO;
                case "==" -> res = left.compareTo(right) == 0 ? BigDecimal.ONE : BigDecimal.ZERO;
                case "!=" -> res = left.compareTo(right) != 0 ? BigDecimal.ONE : BigDecimal.ZERO;
                default -> throw new RuntimeException("Unsupported operator: " + operator);
            }

            work.subList(0, 3).clear();
            work.add(0, res);
        }

        return new BigDecimal(work.get(0).toString());
    }

    public BigDecimal textEval(List<Object> stack) {

        List<Object> work = new ArrayList<>(stack);

        while (work.size() > 1) {

            Object expr0 = work.get(0);
            Object expr1 = work.get(1);
            Object expr2 = work.get(2);

            if (!(expr0 instanceof String) || !(expr1 instanceof String) || !(expr2 instanceof String)) {
                throw new RuntimeException("Invalid text RPN: " + stack);
            }

            String left = (String) expr0;
            String right = (String) expr1;
            String operator = (String) expr2;

            BigDecimal res;

            switch (operator) {
                case "==" -> res = left.equals(right) ? BigDecimal.ONE : BigDecimal.ZERO;
                case "!=" -> res = !left.equals(right) ? BigDecimal.ONE : BigDecimal.ZERO;
                case "contains" -> res = left.contains(right) ? BigDecimal.ONE : BigDecimal.ZERO;
                default -> throw new RuntimeException("Unsupported operator: " + operator);
            }

            work.subList(0, 3).clear();
            work.add(0, res);
        }

        return new BigDecimal(work.get(0).toString());
    }
}