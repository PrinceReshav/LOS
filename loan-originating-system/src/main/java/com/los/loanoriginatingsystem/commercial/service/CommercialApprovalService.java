package com.los.loanoriginatingsystem.commercial.service;

import com.los.loanoriginatingsystem.commercial.client.AdminServiceEmployeeClient;
import com.los.loanoriginatingsystem.commercial.dto.CommercialApprovalActionRequest;
import com.los.loanoriginatingsystem.commercial.dto.CommercialApprovalResolveRequest;
import com.los.loanoriginatingsystem.commercial.entity.CommercialApproval;
import com.los.loanoriginatingsystem.commercial.entity.CommercialMatrix;
import com.los.loanoriginatingsystem.commercial.repository.CommercialApprovalRepository;
import com.los.loanoriginatingsystem.commercial.repository.CommercialMatrixRepository;
import com.los.loanoriginatingsystem.common.exception.ResourceNotFoundException;
import com.los.loanoriginatingsystem.loan.entity.LoanApplication;
import com.los.loanoriginatingsystem.loan.repository.LoanApplicationRepository;
import com.los.loanoriginatingsystem.loanProduct.entity.LoanProduct;
import com.los.loanoriginatingsystem.loanProduct.repository.LoanProductRepository;
import com.los.loanoriginatingsystem.rules.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Resolves and drives the commercial-approval workflow for a loan
 * application: matches the Commercial Matrix, determines whether the
 * loan auto-approves or which role must approve it (escalating to the
 * most senior matched role, exactly as Salesforce's
 * CommercialMatrixHandler.getHighestRole() walked the role hierarchy -
 * here done via UserRole's numeric level instead of a live role-tree
 * walk, since UserRole already encodes the hierarchy depth), optionally
 * auto-assigns eligible branch employees as Approver 1/2, and then
 * tracks the submit/approve/reject lifecycle.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CommercialApprovalService {

    private final CommercialMatrixRepository matrixRepository;
    private final CommercialApprovalRepository approvalRepository;
    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanProductRepository loanProductRepository;
    private final AdminServiceEmployeeClient employeeClient;

    @Transactional
    public CommercialApproval resolve(String loanApplicationId, CommercialApprovalResolveRequest request) {

        LoanApplication app = loanApplicationRepository.findById(loanApplicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan application not found: " + loanApplicationId));

        LoanProduct product = app.getLoanProductId() != null
                ? loanProductRepository.findById(app.getLoanProductId()).orElse(null)
                : null;

        CommercialApproval approval = approvalRepository.findByLoanApplicationId(loanApplicationId)
                .orElseGet(() -> {
                    CommercialApproval a = new CommercialApproval();
                    a.setId(UUID.randomUUID().toString());
                    a.setLoanApplicationId(loanApplicationId);
                    return a;
                });

        // A loan already fully decided should not be silently re-resolved.
        if ("APPROVED".equals(approval.getStatus()) || "REJECTED".equals(approval.getStatus())) {
            throw new IllegalStateException(
                    "Commercial approval for " + loanApplicationId + " is already " + approval.getStatus());
        }

        approval.setCoApplicantWaiver(request.isCoApplicantWaiver());

        BigDecimal creditScoreAsBigDecimal = app.getCreditScore() != null ? BigDecimal.valueOf(app.getCreditScore()) : null;
        BigDecimal roi = app.getRoi() != null ? app.getRoi() : BigDecimal.ZERO;
        BigDecimal fee = app.getProcessingFee() != null ? app.getProcessingFee() : BigDecimal.ZERO;
        BigDecimal total = roi.add(fee);

        if (request.isCoApplicantWaiver()) {
            // Co-applicant waiver always escalates to the most senior approval role,
            // regardless of what the matrix would otherwise resolve to.
            approval.setResolvedRole(UserRole.BUSINESS_HEAD.getCode());
            approval.setMatchedMatrixId(null);
            approval.setStatus("PENDING");

        } else {

            List<CommercialMatrix> matches = matrixRepository.findAll(
                    CommercialMatrixSpecifications.matches(
                            app.getLoanScheme(),
                            app.getLoanType(),
                            product != null && product.getSecuredLoanCategory() != null
                                    ? product.getSecuredLoanCategory().name() : null,
                            app.getLoanProductCode(),
                            app.getCreditScore(),
                            app.getApprovedAmount(),
                            total,
                            fee
                    )
            );

            if (matches.isEmpty()) {
                // No rule configured for this combination - block for manual admin
                // attention rather than silently letting the loan through.
                approval.setResolvedRole(null);
                approval.setMatchedMatrixId(null);
                approval.setStatus("BLOCKED_NO_APPROVER");

            } else {
                boolean anyRequiresHuman = matches.stream().anyMatch(m -> !Boolean.TRUE.equals(m.getAutoApproved()));

                if (!anyRequiresHuman) {
                    // Every matched row is auto-approve, and nothing escalated it -> auto-approve.
                    approval.setResolvedRole(null);
                    approval.setMatchedMatrixId(matches.get(0).getId());
                    approval.setStatus("AUTO_APPROVED");
                    approval.setDecidedAt(LocalDateTime.now());
                    approval.setDecidedBy("SYSTEM_AUTO_APPROVAL");

                } else {
                    CommercialMatrix highest = matches.stream()
                            .filter(m -> !Boolean.TRUE.equals(m.getAutoApproved()))
                            .filter(m -> m.getRequiredRole() != null)
                            .max((a, b) -> Integer.compare(
                                    UserRole.fromCode(a.getRequiredRole()).getLevel(),
                                    UserRole.fromCode(b.getRequiredRole()).getLevel()))
                            .orElseThrow(() -> new IllegalStateException(
                                    "Matched commercial matrix row(s) require human approval but none has a requiredRole set"));

                    approval.setResolvedRole(highest.getRequiredRole());
                    approval.setMatchedMatrixId(highest.getId());
                    approval.setStatus("PENDING");
                }
            }
        }

        approval.setResolvedAt(LocalDateTime.now());

        if (request.isAutoAssignApprovers() && "PENDING".equals(approval.getStatus()) && approval.getResolvedRole() != null) {
            assignApprovers(approval, app.getBranchId());
        }

        return approvalRepository.save(approval);
    }

    private void assignApprovers(CommercialApproval approval, String branchId) {

        List<String> eligible = employeeClient.findEligibleApproverEmployeeIds(branchId, approval.getResolvedRole());

        if (eligible.isEmpty()) {
            approval.setStatus("BLOCKED_NO_APPROVER");
            log.warn("No eligible approver found for role={} at branch={} (loanApplicationId={})",
                    approval.getResolvedRole(), branchId, approval.getLoanApplicationId());
            return;
        }

        approval.setApprover1EmployeeId(eligible.get(0));

        // Mirrors the old Salesforce fallback: if only one qualifying user exists
        // at the branch, they fill both approver slots rather than blocking the
        // loan - this is a real dual-control gap, flagged via the comment field
        // rather than hidden, so reporting can surface single-approver loans.
        if (eligible.size() > 1) {
            approval.setApprover2EmployeeId(eligible.get(1));
        } else {
            approval.setApprover2EmployeeId(eligible.get(0));
            approval.setComment("Single eligible approver at branch - same employee assigned to both approver slots.");
        }
    }

    @Transactional
    public CommercialApproval submit(String loanApplicationId) {
        CommercialApproval approval = getOrThrow(loanApplicationId);

        if (!"PENDING".equals(approval.getStatus())) {
            throw new IllegalStateException("Cannot submit from status: " + approval.getStatus());
        }
        if (approval.getApprover1EmployeeId() == null) {
            throw new IllegalStateException("No approver assigned yet - resolve approvers before submitting");
        }

        approval.setStatus("SUBMITTED");
        approval.setSubmittedAt(LocalDateTime.now());

        return approvalRepository.save(approval);
    }

    @Transactional
    public CommercialApproval approve(String loanApplicationId, CommercialApprovalActionRequest request) {
        CommercialApproval approval = getOrThrow(loanApplicationId);

        if (!"SUBMITTED".equals(approval.getStatus())) {
            throw new IllegalStateException("Cannot approve from status: " + approval.getStatus());
        }

        boolean isApprover1 = request.getEmployeeId() != null && request.getEmployeeId().equals(approval.getApprover1EmployeeId());
        boolean isApprover2 = request.getEmployeeId() != null && request.getEmployeeId().equals(approval.getApprover2EmployeeId());

        if (!isApprover1 && !isApprover2) {
            throw new IllegalStateException("Employee " + request.getEmployeeId() + " is not an assigned approver for this loan");
        }

        if (isApprover1) approval.setApprover1Approved(true);
        if (isApprover2) approval.setApprover2Approved(true);

        boolean sameApprover = approval.getApprover1EmployeeId() != null
                && approval.getApprover1EmployeeId().equals(approval.getApprover2EmployeeId());

        boolean fullyApproved = sameApprover
                ? Boolean.TRUE.equals(approval.getApprover1Approved())
                : Boolean.TRUE.equals(approval.getApprover1Approved()) && Boolean.TRUE.equals(approval.getApprover2Approved());

        if (fullyApproved) {
            approval.setStatus("APPROVED");
            approval.setDecidedAt(LocalDateTime.now());
            approval.setDecidedBy(request.getEmployeeId());
        }

        if (request.getComment() != null) {
            approval.setComment(request.getComment());
        }

        return approvalRepository.save(approval);
    }

    @Transactional
    public CommercialApproval reject(String loanApplicationId, CommercialApprovalActionRequest request) {
        CommercialApproval approval = getOrThrow(loanApplicationId);

        if ("APPROVED".equals(approval.getStatus()) || "REJECTED".equals(approval.getStatus())) {
            throw new IllegalStateException("Cannot reject from status: " + approval.getStatus());
        }

        approval.setStatus("REJECTED");
        approval.setDecidedAt(LocalDateTime.now());
        approval.setDecidedBy(request.getEmployeeId());
        approval.setComment(request.getComment());

        return approvalRepository.save(approval);
    }

    public CommercialApproval get(String loanApplicationId) {
        return getOrThrow(loanApplicationId);
    }

    private CommercialApproval getOrThrow(String loanApplicationId) {
        return approvalRepository.findByLoanApplicationId(loanApplicationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No commercial approval resolved yet for loan application: " + loanApplicationId));
    }
}
