package com.los.loanoriginatingsystem.rules.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RuleEngineRequest {

    @NotBlank private String name;

    /** e.g. "LoanApplication", "UnderwritingCase" - the target this rule evaluates against. */
    @NotBlank private String objectApiName;

    private Boolean isDefault;

    /** "AND" / "OR" - combination used when customLogic is blank. */
    private String operator;

    /** Optional custom boolean expression referencing criteria by sequence, e.g. "1 AND (2 OR 3)". */
    private String customLogic;

    @NotNull private Integer deviationLevel;
}
