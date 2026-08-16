package com.los.loanoriginatingsystem.commercial.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CommercialMatrixRequest {

    private String name;

    /** Null/blank = applies to all schemes. */
    private String scheme;

    /** SECURED / UNSECURED. Null = applies to both. */
    private String loanType;

    private String securedLoanCategory;
    private String productCode;

    private Integer minCreditScore;
    private Integer maxCreditScore;

    private BigDecimal minLoanAmount;
    private BigDecimal maxLoanAmount;

    private BigDecimal minTotal;
    private BigDecimal maxTotal;

    private BigDecimal minProcessingFee;
    private BigDecimal maxProcessingFee;

    /** Required unless autoApproved = true. */
    private String requiredRole;

    @NotNull
    private Boolean autoApproved;

    private BigDecimal maxProcessingFeesAllowed;
}
