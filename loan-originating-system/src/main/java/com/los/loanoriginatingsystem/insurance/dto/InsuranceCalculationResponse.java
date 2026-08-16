package com.los.loanoriginatingsystem.insurance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsuranceCalculationResponse {
    private BigDecimal lifeInsurancePremium;
    private BigDecimal lifeInsuranceRateUsed;
    private BigDecimal propertyInsurancePremium;
    private BigDecimal propertyInsuranceRateUsed;
    private BigDecimal totalInsurancePremium;
}
