package com.los.loanoriginatingsystem.temp.controller;

import com.los.loanoriginatingsystem.temp.dto.ApplicantAutoFillDTO;
import com.los.loanoriginatingsystem.temp.service.ApplicantAutoFillService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/temp")
@RequiredArgsConstructor
public class ApplicantAutoFillController {

    private final ApplicantAutoFillService service;

    // =====================================================
    // 🔥 AUTO-FILL API
    // =====================================================
    @GetMapping("/{tempId}/applicant/autofill")
    public ResponseEntity<ApplicantAutoFillDTO> getAutoFill(
            @PathVariable String tempId
    ) {
        if (tempId == null || tempId.isBlank()) {
            throw new RuntimeException("Invalid tempId");
        }

        return ResponseEntity.ok(service.getAutoFillData(tempId));
    }
}