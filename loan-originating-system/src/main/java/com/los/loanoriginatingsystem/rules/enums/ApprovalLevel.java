package com.los.loanoriginatingsystem.rules.enums;


public enum ApprovalLevel {

    L0(0),
    L1(1),
    L2(2),
    L3(3),
    L4(4),
    L5(5);

    private final int level;

    ApprovalLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public static ApprovalLevel from(int level) {
        for (ApprovalLevel l : values()) {
            if (l.level == level) return l;
        }
        throw new IllegalArgumentException("Invalid level: " + level);
    }
}