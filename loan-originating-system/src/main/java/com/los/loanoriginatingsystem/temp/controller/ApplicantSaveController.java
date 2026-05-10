package com.los.loanoriginatingsystem.temp.controller;

import com.los.loanoriginatingsystem.temp.dto.ApplicantSaveRequestDTO;
import com.los.loanoriginatingsystem.temp.service.ApplicantSaveService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/temp")
@RequiredArgsConstructor
public class ApplicantSaveController {

    private final ApplicantSaveService service;

    // =====================================================
    // 🔥 SAVE APPLICANT DATA
    // =====================================================
    @PostMapping("/{tempId}/applicant")
    public ResponseEntity<String> saveApplicant(
            @PathVariable String tempId,
            @RequestBody ApplicantSaveRequestDTO request
    ) {
        service.saveApplicant(tempId, request);
        return ResponseEntity.ok("Applicant saved successfully");
    }
}