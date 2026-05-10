package com.los.loanoriginatingsystem.loanProduct.config.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "loan_eligibility_config")
@Data
public class LoanEligibilityConfig {

    @Id
    private String id;

    private String productCode;
    private String loanType;
    private String loanScheme;
    private String loanCategory;

    private Integer minTenure;
    private Integer maxTenure;

    private Integer minCreditScore;
    private Integer maxCreditScore;

    private BigDecimal minLoanAmount;
    private BigDecimal maxLoanAmount;
}