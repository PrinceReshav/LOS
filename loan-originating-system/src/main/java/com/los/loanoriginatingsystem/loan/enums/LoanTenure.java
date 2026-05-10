package com.los.loanoriginatingsystem.loan.enums;

import lombok.Getter;

@Getter
public enum LoanTenure {

    T12(12),
    T24(24),
    T36(36),
    T48(48),
    T60(60),
    T72(72);

    private final int months;

    LoanTenure(int months) {
        this.months = months;
    }

}