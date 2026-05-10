package com.los.loanoriginatingsystem.loanProduct.entity;


import com.los.loanoriginatingsystem.loanProduct.entity.enums.*;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "loan_product")
@Data
public class LoanProduct {

    @Id
    private String id;

    private String name;

    // =============================
    // CORE CONFIG
    // =============================

    @Enumerated(EnumType.STRING)
    private LoanType loanType;

    @Enumerated(EnumType.STRING)
    private LoanScheme loanScheme;

    @Enumerated(EnumType.STRING)
    private ProductCode productCode;

    @Enumerated(EnumType.STRING)
    private SecuredLoanCategory securedLoanCategory;

    @Enumerated(EnumType.STRING)
    private CommercialType commercialType;

    private Boolean isActive;

    // =============================
    // LOAN RULES
    // =============================

    private BigDecimal minLoanAmount;
    private BigDecimal maxLoanAmount;

    private Integer minimumAgeBorrower;

    // =============================
    // INTEREST & FEES
    // =============================

    private BigDecimal fixedInterestRate;
    private BigDecimal fixedProcessingFees;
    private BigDecimal maxProcessingFees;
    private BigDecimal insurancePercent;

    // =============================
    // DOCUMENT RULES
    // =============================

    @ElementCollection(targetClass = ApplicantDocumentType.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "loan_product_applicant_docs")
    private Set<ApplicantDocumentType> applicantDocuments;

    @ElementCollection(targetClass = ApplicationDocumentType.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "loan_product_application_docs")
    private Set<ApplicationDocumentType> applicationDocuments;

    // =============================
    // LMS MAPPING
    // =============================

    private String lmsProductId;
    private String lmsPurposeCategoryId;
    private String purposeCodeId;

    private Boolean approvalRequired;









        // =============================
        // COMMERCIAL
        // =============================
        private String commercialData; // Fixed / Manual

    }