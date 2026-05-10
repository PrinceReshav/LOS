package com.los.loanoriginatingsystem.monitoring.controller;

import com.los.loanoriginatingsystem.monitoring.service.AdminRetryService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminRetryService service;

    // =============================
    // 🔁 RETRY FROM DLQ
    // =============================
    @PostMapping("/retry/{tempId}")
    public String retry(@PathVariable String tempId) {
        return service.retryFromDLQ(tempId);
    }

    // =============================
    // ⚠️ FORCE COMPLETE
    // =============================
    @PostMapping("/force-complete/{tempId}")
    public String forceComplete(
            @PathVariable String tempId,
            @RequestParam String loanId
    ) {
        return service.forceComplete(tempId, loanId);
    }
}