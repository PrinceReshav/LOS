package com.los.loanoriginatingsystem.loan.enums;

public enum LoanStage {

    DATA_ENTRY,
    UNDERWRITING,
    PRE_SANCTION,
    SANCTION,
    DISBURSEMENT;

    public LoanStage next() {

        int nextOrdinal = this.ordinal() + 1;

        LoanStage[] values = LoanStage.values();

        if (nextOrdinal >= values.length) {
            throw new IllegalStateException(
                    "Already at the final stage : " + this
            );
        }

        return values[nextOrdinal];
    }

    public boolean isBefore(LoanStage other) {
        return this.ordinal() < other.ordinal();
    }

    public boolean isAfterOrEqual(LoanStage other) {
        return this.ordinal() >= other.ordinal();
    }
}
