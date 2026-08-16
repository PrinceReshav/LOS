package com.los.loanoriginatingsystem.loanProduct.dto;

import com.los.loanoriginatingsystem.loanProduct.entity.enums.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Full editable payload for creating/updating a LoanProduct - this is the
 * admin-facing "Loan Product" screen contract. `id` is generated server
 * side on create and immutable on update.
 */
@Data
public class LoanProductRequest {

    @NotBlank
    private String name;

    @NotNull private LoanType loanType;
    @NotNull private LoanScheme loanScheme;
    @NotNull private ProductCode productCode;
    private SecuredLoanCategory securedLoanCategory;
    private CommercialType commercialType;

    @NotNull private BigDecimal minLoanAmount;
    @NotNull private BigDecimal maxLoanAmount;

    private Integer minimumAgeBorrower;

    private BigDecimal fixedInterestRate;
    private BigDecimal fixedProcessingFees;
    private BigDecimal maxProcessingFees;
    private BigDecimal insurancePercent;

    private Set<ApplicantDocumentType> applicantDocuments;
    private Set<ApplicationDocumentType> applicationDocuments;

    private String lmsProductId;
    private String lmsPurposeCategoryId;
    private String purposeCodeId;

    private Boolean approvalRequired;
    private String commercialData;
}
