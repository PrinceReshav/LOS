package com.los.loanoriginatingsystem.commercial.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * The resolved commercial-approval state for a single loan application -
 * one row per LoanApplication. Kept as its own entity (rather than bolted
 * onto the already-wide LoanApplication) so approval workflow state,
 * approver assignment, and audit timestamps live separately from the
 * loan's core data.
 *
 * Mirrors LoanApplication__c.Commercial_Approval_Status__c / Approver_1__c /
 * Approver_2__c / Co_Applicant_Waiver__c from the old Salesforce org.
 */
@Entity
@Table(name = "commercial_approval", indexes = {
        @Index(name = "idx_commercial_approval_loan_app", columnList = "loan_application_id", unique = true)
})
@Getter
@Setter
public class CommercialApproval {

    @Id
    private String id;

    @Column(name = "loan_application_id", nullable = false, unique = true)
    private String loanApplicationId;

    /** The CommercialMatrix.id whose match produced this resolution (nullable if auto-approved with no row saved). */
    @Column(name = "matched_matrix_id")
    private String matchedMatrixId;

    /** Role code that was resolved as required to approve - see rules.enums.UserRole. Null if auto-approved. */
    @Column(name = "resolved_role")
    private String resolvedRole;

    @Column(name = "approver1_employee_id")
    private String approver1EmployeeId;

    @Column(name = "approver1_approved")
    private Boolean approver1Approved = false;

    @Column(name = "approver2_employee_id")
    private String approver2EmployeeId;

    @Column(name = "approver2_approved")
    private Boolean approver2Approved = false;

    /**
     * When true, standard co-applicant requirement is waived - per the old
     * Salesforce rule this always forces escalation to the most senior
     * ("Business Head") approval role regardless of what the matrix would
     * otherwise resolve to.
     */
    @Column(name = "co_applicant_waiver")
    private Boolean coApplicantWaiver = false;

    /** PENDING / SUBMITTED / AUTO_APPROVED / APPROVED / REJECTED / BLOCKED_NO_APPROVER */
    @Column(nullable = false)
    private String status = "PENDING";

    private String comment;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "decided_at")
    private LocalDateTime decidedAt;

    @Column(name = "decided_by")
    private String decidedBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (status == null) status = "PENDING";
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
