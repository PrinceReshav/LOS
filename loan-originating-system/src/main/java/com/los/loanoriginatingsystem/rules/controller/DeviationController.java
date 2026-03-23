package com.los.loanoriginatingsystem.rules.controller;

import com.los.loanoriginatingsystem.rules.engine.deviation.ApprovalEngine;
import com.los.loanoriginatingsystem.rules.entity.DeviationAudit;
import com.los.loanoriginatingsystem.rules.model.DeviationResponseDTO;
import com.los.loanoriginatingsystem.rules.repository.DeviationAuditRepository;
import com.los.loanoriginatingsystem.rules.service.DeviationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deviations")
@RequiredArgsConstructor
public class DeviationController {

    private final DeviationService deviationService;
    private final ApprovalEngine approvalEngine;
    private final DeviationAuditRepository auditRepository;

    /**
     * 🔥 GET ALL PENDING DEVIATIONS
     */
    @GetMapping("/{applicationId}")
    public List<DeviationResponseDTO> getDeviations(
            @PathVariable String applicationId) {

        return deviationService.getPendingDeviations(applicationId);
    }

    /**
     * 🔥 COUNT
     */
    @GetMapping("/{applicationId}/count")
    public long countPending(@PathVariable String applicationId) {

        return deviationService.countPending(applicationId);
    }

    /**
     * 🔥 APPROVE
     */
    @PostMapping("/{id}/approve")
    public String approve(
            @PathVariable String id,
            HttpServletRequest request
    ) {

        String user = (String) request.getAttribute("user");
        String role = (String) request.getAttribute("role");

        if (user == null || role == null) {
            throw new RuntimeException("Unauthorized request");
        }

        approvalEngine.approve(id, role, user);

        return "Deviation approved successfully";
    }

    /**
     * 🔥 REJECT
     */
    @PostMapping("/{id}/reject")
    public String reject(
            @PathVariable String id,
            @RequestParam(required = false) String comment,
            HttpServletRequest request
    ) {

        String user = (String) request.getAttribute("user");

        if (user == null) {
            throw new RuntimeException("Unauthorized request");
        }

        approvalEngine.reject(id, user, comment);

        return "Deviation rejected successfully";
    }

    /**
     * 🔥 AUDIT HISTORY
     */
    @GetMapping("/{id}/audit")
    public List<DeviationAudit> getAudit(@PathVariable String id) {

        return auditRepository.findByDeviationId(id);
    }
}