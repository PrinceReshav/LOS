package com.los.loanoriginatingsystem.loan.config.entity;



import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "loan_eligibility_config")
@Data
public class LoanEligibilityConfig {

    @Id
    private String id;

    private String productCode;     // MBL, PBL
    private String loanType;        // Secured / Unsecured
    private String loanScheme;      // ABB / GST / ITR
    private String loanCategory;    // IMPL / Perfect Secured

    private Integer minTenure;
    private Integer maxTenure;

    private Integer minCreditScore;
    private Integer maxCreditScore;

    private BigDecimal minAmount;
    private BigDecimal maxAmount;
}
