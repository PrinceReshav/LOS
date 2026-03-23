package com.los.loanoriginatingsystem.rules.engine.eval;

public enum Operator {

    // Numeric
    GT(">"),
    LT("<"),
    GTE(">="),
    LTE("<="),
    EQ("=="),
    NE("!="),

    // String
    CONTAINS("contains");

    private final String symbol;

    Operator(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public static Operator from(String op) {
        for (Operator operator : values()) {
            if (operator.symbol.equalsIgnoreCase(op)) {
                return operator;
            }
        }
        throw new IllegalArgumentException("Unsupported operator: " + op);
    }
}