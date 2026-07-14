package com.los.loanoriginatingsystem.temp.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SaveLoanDetailsRequestDTO {

    private String loanPurpose;

    private String loanType;

    private String loanProductId;

    private String loanScheme;

    private BigDecimal requestedAmount;

    private Integer tenureMonths;
}