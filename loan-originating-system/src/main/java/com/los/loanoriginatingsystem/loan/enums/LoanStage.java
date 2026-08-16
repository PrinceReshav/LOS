package com.los.loanoriginatingsystem.loan.enums;

/**
 * The loan application's primary workflow stage.
 *
 * DATA_ENTRY -> UNDERWRITING -> PRE_SANCTION -> SANCTION ->
 * PRE_DISBURSAL_REVIEW -> INITIATE_DISBURSEMENT -> DISBURSED
 *
 * REJECTED is a special terminal stage reachable from any non-terminal
 * stage (see LoanStageTransitionRules) - equivalent to the old Salesforce
 * org's Stage__c = 'Rejected', which could be set from almost any point in
 * the pipeline.
 *
 * Unlike the previous version of this enum, stage order/adjacency is no
 * longer implied by ordinal() - see LoanStageTransitionRules for the
 * explicit transition graph, since REJECTED breaks a purely linear model.
 */
public enum LoanStage {

    DATA_ENTRY,
    UNDERWRITING,
    PRE_SANCTION,
    SANCTION,
    PRE_DISBURSAL_REVIEW,
    INITIATE_DISBURSEMENT,
    DISBURSED,
    REJECTED;

    public boolean isTerminal() {
        return this == DISBURSED || this == REJECTED;
    }
}
