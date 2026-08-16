package com.los.loanoriginatingsystem.insurance.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PropertyInsuranceRateRequest {

    @NotNull private Integer policyTenureMonths;
    @NotNull private BigDecimal percentageIncGst;
    private String description;
}
