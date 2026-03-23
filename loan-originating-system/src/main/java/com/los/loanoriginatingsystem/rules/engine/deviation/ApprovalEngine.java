package com.los.loanoriginatingsystem.rules.engine.deviation;

import com.los.loanoriginatingsystem.notification.client.NotificationClient;
import com.los.loanoriginatingsystem.notification.dto.NotificationRequest;
import com.los.loanoriginatingsystem.rules.entity.ApprovalMatrix;
import com.los.loanoriginatingsystem.rules.entity.Deviation;
import com.los.loanoriginatingsystem.rules.entity.DeviationAudit;
import com.los.loanoriginatingsystem.rules.repository.ApprovalMatrixRepository;
import com.los.loanoriginatingsystem.rules.repository.DeviationAuditRepository;
import com.los.loanoriginatingsystem.rules.repository.DeviationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApprovalEngine {

    private final ApprovalMatrixRepository matrixRepo;
    private final DeviationRepository deviationRepository;
    private final DeviationAuditRepository auditRepository;
    private final NotificationClient  notificationClient;

    // ==========================================
    // 🔥 APPROVE FLOW
    // ==========================================
    @Transactional
    public void approve(String deviationId, String roleCode, String userName) {

        Deviation dev = deviationRepository.findById(deviationId)
                .orElseThrow(() -> new RuntimeException("Deviation not found: " + deviationId));

        // 🔒 Prevent re-processing
        if ("APPROVED".equalsIgnoreCase(dev.getFinalStatus()) ||
                "REJECTED".equalsIgnoreCase(dev.getFinalStatus())) {
            throw new RuntimeException("Deviation already closed");
        }

        // 🔥 Load approval steps
        List<ApprovalMatrix> steps =
                matrixRepo.findByLevelOrderBySequenceAsc(dev.getDeviationLevel());

        if (steps == null || steps.isEmpty()) {
            throw new RuntimeException(
                    "Approval matrix not configured for level: " + dev.getDeviationLevel()
            );
        }

        // 🔥 Resolve current level safely
        Integer currentLevel = dev.getCurrentLevel() != null
                ? dev.getCurrentLevel()
                : steps.get(0).getLevel();

        int currentIndex = findCurrentStep(currentLevel, steps);

        ApprovalMatrix currentStep = steps.get(currentIndex);

        // 🔥 Normalize role (production safe)
        String normalizedRole = roleCode == null ? "" : roleCode.trim().toUpperCase();
        String requiredRole = currentStep.getRequiredRole() == null
                ? ""
                : currentStep.getRequiredRole().trim().toUpperCase();

        if (!requiredRole.equals(normalizedRole)) {
            throw new RuntimeException(
                    "Unauthorized: required=" + requiredRole + ", provided=" + normalizedRole
            );
        }

        // 🔥 Move to next step OR final approval
        if (currentIndex + 1 < steps.size()) {

            ApprovalMatrix nextStep = steps.get(currentIndex + 1);

            dev.setCurrentLevel(nextStep.getLevel());
            dev.setStatus(formatLevel(nextStep.getLevel()) + "_PENDING");

            log.info("Deviation {} moved to next level L{}", dev.getId(), nextStep.getLevel());

        } else {
            dev.setStatus("APPROVED");
            dev.setFinalStatus("APPROVED");

            log.info("Deviation {} fully APPROVED", dev.getId());
        }

        dev.setApprovedBy(userName);
        dev.setApprovedAt(LocalDateTime.now());
        dev.setLastUpdatedAt(LocalDateTime.now());

        deviationRepository.save(dev);

        // 🔥 Audit should NEVER break main flow
        try {
            logAudit(dev.getId(), "APPROVED", dev.getCurrentLevel(), userName, null);
        } catch (Exception e) {
            log.error("Audit failed for deviation {}", dev.getId(), e);
        }

        notificationClient.send(
                new NotificationRequest(
                        "EMAIL",
                        List.of("manager@company.com"),
                        "Deviation Approved",
                        "Deviation " + dev.getId() + " approved",
                        Map.of("deviationId", dev.getId())
                )
        );
    }

    // ==========================================
    // 🔥 REJECT FLOW
    // ==========================================
    @Transactional
    public void reject(String deviationId, String userName, String comment) {

        Deviation dev = deviationRepository.findById(deviationId)
                .orElseThrow(() -> new RuntimeException("Deviation not found: " + deviationId));

        if ("APPROVED".equalsIgnoreCase(dev.getFinalStatus()) ||
                "REJECTED".equalsIgnoreCase(dev.getFinalStatus())) {
            throw new RuntimeException("Deviation already closed");
        }

        dev.setStatus("REJECTED");
        dev.setFinalStatus("REJECTED");
        dev.setComment(comment);
        dev.setApprovedBy(userName);
        dev.setApprovedAt(LocalDateTime.now());
        dev.setLastUpdatedAt(LocalDateTime.now());

        deviationRepository.save(dev);

        log.info("Deviation {} REJECTED by {}", dev.getId(), userName);

        try {
            logAudit(dev.getId(), "REJECTED", dev.getCurrentLevel(), userName, comment);
        } catch (Exception e) {
            log.error("Audit failed for deviation {}", dev.getId(), e);
        }
    }

    // ==========================================
    // 🔥 STEP FINDER
    // ==========================================
    private int findCurrentStep(Integer level, List<ApprovalMatrix> steps) {

        for (int i = 0; i < steps.size(); i++) {
            if (steps.get(i).getLevel().equals(level)) {
                return i;
            }
        }

        throw new RuntimeException("Invalid current level: " + level);
    }

    // ==========================================
    // 🔥 AUDIT LOGGER
    // ==========================================
    private void logAudit(String deviationId,
                          String action,
                          Integer level,
                          String user,
                          String comment) {

        DeviationAudit audit = new DeviationAudit();

        audit.setId(UUID.randomUUID().toString());
        audit.setDeviationId(deviationId);
        audit.setAction(action);
        audit.setLevel(level);
        audit.setPerformedBy(user);
        audit.setPerformedAt(LocalDateTime.now());
        audit.setComment(comment);

        auditRepository.save(audit);
    }

    // ==========================================
    // 🔥 HELPER
    // ==========================================
    private String formatLevel(Integer level) {
        return "L" + level;
    }
}