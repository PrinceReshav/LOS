package com.los.loanoriginatingsystem.commercial.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Commercial approval-routing matrix: given a loan's scheme, type,
 * category, credit score, approved amount, and rate+fee band, resolves
 * which role must approve the commercial terms (or whether the loan
 * auto-approves).
 *
 * Equivalent to Salesforce's Commercial_Matrix__mdt + the
 * CommercialMatrixHandler.returnCommMatrixBased() query, rebuilt as a
 * real admin-editable table queried through a JPA Specification instead
 * of string-concatenated SOQL.
 *
 * A null bound on any *Min/*Max field means "no restriction on that side" -
 * e.g. minCreditScore=null, maxCreditScore=750 matches any score <= 750.
 * scheme/loanType/securedLoanCategory/productCode = null means "applies to
 * all values" (the Java equivalent of Salesforce's partial/LIKE scheme match).
 */
@Entity
@Table(name = "commercial_matrix")
@Getter
@Setter
public class CommercialMatrix {

    @Id
    private String id;

    private String name;

    /** Null = applies to every scheme. Otherwise must equal LoanApplication.loanScheme. */
    private String scheme;

    /** SECURED / UNSECURED. Null = applies to both. */
    private String loanType;

    private String securedLoanCategory;

    private String productCode;

    private Integer minCreditScore;
    private Integer maxCreditScore;

    @Column(precision = 15, scale = 2)
    private BigDecimal minLoanAmount;
    @Column(precision = 15, scale = 2)
    private BigDecimal maxLoanAmount;

    /** Combined rate + processing-fee band (mirrors the SF "Total" band). */
    @Column(precision = 8, scale = 4)
    private BigDecimal minTotal;
    @Column(precision = 8, scale = 4)
    private BigDecimal maxTotal;

    @Column(precision = 8, scale = 4)
    private BigDecimal minProcessingFee;
    @Column(precision = 8, scale = 4)
    private BigDecimal maxProcessingFee;

    /**
     * Role code required to approve a loan matching this row - see
     * rules.enums.UserRole (CBM, CCM, DBM, DCM, ZBM, ZCM, BH, DY_CEO, CEO, MD).
     * Ignored (may be null) when autoApproved = true.
     */
    private String requiredRole;

    /**
     * If true, a loan matching only this row (and no other, non-auto-approved
     * row) needs no human approver. Explicit boolean instead of Salesforce's
     * magic string Role__c = 'AutoApproved'.
     */
    @Column(name = "auto_approved", nullable = false)
    private Boolean autoApproved = false;

    /** Max processing fee allowed without further escalation, if this row applies. */
    @Column(precision = 8, scale = 4)
    private BigDecimal maxProcessingFeesAllowed;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (active == null) active = true;
        if (autoApproved == null) autoApproved = false;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
