package com.los.loanoriginatingsystem.rules.engine.expression;

import org.springframework.stereotype.Component;

import java.util.Stack;

@Component
public class BooleanExpressionEvaluator {

    /**
     * Entry method
     */
    public boolean evaluate(String expression) {

        if (expression == null || expression.isBlank()) {
            return false;
        }

        // Normalize
        expression = expression
                .replaceAll("AND", "&&")
                .replaceAll("OR", "||")
                .replaceAll("TRUE", "true")
                .replaceAll("FALSE", "false");

        return evaluateExpression(expression);
    }

    /**
     * Shunting-yard based evaluation
     */
    private boolean evaluateExpression(String expr) {

        Stack<Boolean> values = new Stack<>();
        Stack<String> ops = new Stack<>();

        String[] tokens = expr
                .replace("(", " ( ")
                .replace(")", " ) ")
                .trim()
                .split("\\s+");

        for (String token : tokens) {

            switch (token) {

                case "true":
                    values.push(true);
                    break;

                case "false":
                    values.push(false);
                    break;

                case "(":
                    ops.push(token);
                    break;

                case ")":
                    while (!ops.isEmpty() && !ops.peek().equals("(")) {
                        applyOp(values, ops.pop());
                    }
                    ops.pop();
                    break;

                case "&&":
                case "||":
                    while (!ops.isEmpty() && precedence(ops.peek()) >= precedence(token)) {
                        applyOp(values, ops.pop());
                    }
                    ops.push(token);
                    break;

                default:
                    throw new RuntimeException("Invalid token: " + token);
            }
        }

        while (!ops.isEmpty()) {
            applyOp(values, ops.pop());
        }

        return values.pop();
    }

    private int precedence(String op) {
        if (op.equals("&&")) return 2;
        if (op.equals("||")) return 1;
        return 0;
    }

    private void applyOp(Stack<Boolean> values, String op) {

        boolean b = values.pop();
        boolean a = values.pop();

        switch (op) {
            case "&&":
                values.push(a && b);
                break;
            case "||":
                values.push(a || b);
                break;
            default:
                throw new RuntimeException("Invalid operator: " + op);
        }
    }
}