package com.los.loanoriginatingsystem.loan.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_application")
@Data
public class LoanApplication {

    @Id
    private String id;

    // =====================================================
    // IDENTIFIERS
    // =====================================================

    @Column(unique = true, nullable = false)
    private String applicationNumber;

    @Column(unique = true)
    private String loanAccountNumber;

    private String leadId;

    private String tempId;

    // =====================================================
    // APPLICANT
    // =====================================================

    private String primaryApplicantId;

    private String applicantName;

    private String mobileNumber;

    private String email;

    // =====================================================
    // LOAN DETAILS
    // =====================================================

    private String loanPurpose;

    private String loanType;

    private String loanProductId;

    private String loanProductCode;

    private String loanScheme;

    private BigDecimal requestedAmount;

    private BigDecimal approvedAmount;

    private Integer tenureMonths;

    private BigDecimal roi;

    private BigDecimal emiAmount;

    // =====================================================
    // BRANCH DETAILS
    // =====================================================

    private String branchId;

    private String branchCode;

    private String branchName;

    private String rmId;

    private String bcmId;

    private String ccmId;

    // =====================================================
    // BANK DETAILS
    // =====================================================

    private String bankName;

    private String accountHolderName;

    private String accountNumber;

    private String ifscCode;

    private String accountType;

    private String accountHolderType;

    private String branchNameAddress;

    private BigDecimal averageBankBalance;

    private BigDecimal annualisedTurnover;

    // =====================================================
    // ACCOUNT AGGREGATOR
    // =====================================================

    private String accountAggregatorFileNo;

    private String accountAggregatorDocId;

    private String accountAggregatorStatus;

    private String accountAggregatorUrl;

    // =====================================================
    // CREDIT
    // =====================================================

    private Integer creditScore;

    private String creditScoreStatus;

    private BigDecimal eligibleLoanAmount;

    private BigDecimal netDisposableAmount;

    private BigDecimal currentObligations;

    private BigDecimal collateralValue;

    // =====================================================
    // UNDERWRITING
    // =====================================================

    private String underwritingStatus;

    private String underwritingRemarks;

    private String approvalStatus;

    private String rejectCode;

    private String rejectReason;

    // =====================================================
    // LMS
    // =====================================================

    private String lmsApplicationId;

    private String lmsClientId;

    private String lmsStatus;

    private String lmsStage;

    private String lmsLoanAccountNo;

    // =====================================================
    // DISBURSEMENT
    // =====================================================

    private String disbursementStatus;

    private LocalDateTime disbursementDate;

    private BigDecimal disbursedAmount;

    // =====================================================
    // INSURANCE
    // =====================================================

    private BigDecimal insuranceAmount;

    private BigDecimal processingFee;

    private BigDecimal stampCharges;

    private BigDecimal otherCharges;

    // =====================================================
    // WORKFLOW
    // =====================================================

    private String stage;

    private String subStage;

    private Boolean isKycCompleted;

    private Boolean isApplicantCompleted;

    private Boolean isUnderwritingCompleted;

    private Boolean isDisbursed;

    // =====================================================
    // AUDIT
    // =====================================================

    private LocalDateTime applicationDate;

    private LocalDateTime sanctionedDate;

    private LocalDateTime rejectedDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    private String updatedBy;
}