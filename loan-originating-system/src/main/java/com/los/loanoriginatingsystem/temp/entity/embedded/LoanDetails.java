package com.los.loanoriginatingsystem.temp.entity.embedded;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.math.BigDecimal;

@Embeddable
@Data
public class LoanDetails {

    private String loanPurpose;

    private String loanType;

    private String loanProductId;

    private String loanProductCode;   // MBL / PBL (USED IN VALIDATION)

    private String loanScheme;

    private BigDecimal requestedAmount;

    private Integer tenureMonths;
}