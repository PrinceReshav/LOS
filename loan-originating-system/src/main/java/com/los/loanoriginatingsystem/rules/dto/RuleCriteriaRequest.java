package com.los.loanoriginatingsystem.rules.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RuleCriteriaRequest {

    @NotBlank private String fieldName;

    /** e.g. EQUALS, NOT_EQUALS, GREATER_THAN, LESS_THAN, CONTAINS - see rules.engine.eval.Operator. */
    @NotBlank private String operator;

    /** Literal comparison value. Ignored if fieldCompare=true. */
    private String value;

    /** When true, `value` is interpreted as another field name on the same record instead of a literal. */
    private Boolean fieldCompare;

    @NotNull private Integer sequence;
}
