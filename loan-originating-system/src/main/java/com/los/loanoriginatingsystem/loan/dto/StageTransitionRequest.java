package com.los.loanoriginatingsystem.loan.dto;

import com.los.loanoriginatingsystem.loan.enums.LoanStage;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StageTransitionRequest {

    @NotNull
    private LoanStage targetStage;

    /** Optional for forward transitions, mandatory for REJECTED (enforced in service). */
    private String remarks;
}
