package com.los.loanoriginatingsystem.audit.controller;

import com.los.loanoriginatingsystem.audit.dto.AuditResponseDTO;
import com.los.loanoriginatingsystem.audit.service.AuditQueryService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditQueryService service;

    // =============================
    // 🔍 BY TEMP ID
    // =============================
    @GetMapping("/temp/{tempId}")
    public List<AuditResponseDTO> getTempAudit(
            @PathVariable String tempId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return service.getAudit(
                "TEMP",
                tempId,
                action,
                parse(from),
                parse(to),
                page,
                size
        );
    }

    // =============================
    // 🔍 BY LOAN ID
    // =============================
    @GetMapping("/loan/{loanId}")
    public List<AuditResponseDTO> getLoanAudit(
            @PathVariable String loanId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return service.getAudit(
                "LOAN",
                loanId,
                action,
                parse(from),
                parse(to),
                page,
                size
        );
    }

    @GetMapping("/temp/{tempId}/export")
    public String export(
            @PathVariable String tempId
    ) {

        var list = service.getAudit("TEMP", tempId, null, null, null, 0, 1000);

        return service.exportCsv(list);
    }

    private LocalDateTime parse(String input) {
        return input == null ? null : LocalDateTime.parse(input);
    }
}