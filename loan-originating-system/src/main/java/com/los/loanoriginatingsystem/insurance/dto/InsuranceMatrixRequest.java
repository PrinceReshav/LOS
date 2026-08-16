package com.los.loanoriginatingsystem.insurance.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class InsuranceMatrixRequest {

    @NotNull private Integer minAge;
    @NotNull private Integer maxAge;
    @NotNull private Integer tenureMonths;
    @NotNull private BigDecimal flatRate;
    private BigDecimal flatReducedRate;
    private String description;
}
