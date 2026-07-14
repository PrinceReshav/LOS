package com.los.loanoriginatingsystem.temp.controller;

import com.los.loanoriginatingsystem.kyc.aadhaar.dto.*;
import com.los.loanoriginatingsystem.kyc.drivinglicense.dto.*;
import com.los.loanoriginatingsystem.kyc.pan.dto.*;
import com.los.loanoriginatingsystem.kyc.voterid.dto.*;
import com.los.loanoriginatingsystem.temp.service.kyc.KycTempService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/temp/kyc")
@RequiredArgsConstructor
public class TempKycController {

    private final KycTempService service;

    @PostMapping("/{tempId}/aadhaar/upload")
    public void uploadAadhaar(
            @PathVariable String tempId,
            @RequestBody AadhaarOCRResponseDTO dto
    ) {
        service.processAadhaarOCR(tempId, dto);
    }

    @PostMapping("/{tempId}/aadhaar/verify")
    public void verifyAadhaar(
            @PathVariable String tempId,
            @RequestBody AadhaarSubmitOtpResponseDTO dto
    ) {
        service.verifyAadhaarOtp(tempId, dto);
    }

    @PostMapping("/{tempId}/pan/upload")
    public void uploadPan(
            @PathVariable String tempId,
            @RequestBody PanOCRResponseDTO dto
    ) {
        service.processPanOCR(tempId, dto);
    }

    @PostMapping("/{tempId}/pan/verify")
    public void verifyPan(
            @PathVariable String tempId,
            @RequestBody PanVerificationResponseDTO dto
    ) {
        service.verifyPan(tempId, dto);
    }

    @PostMapping("/{tempId}/dl/upload")
    public void uploadDl(
            @PathVariable String tempId,
            @RequestBody DLOCRResponseDTO dto
    ) {
        service.processDlOCR(tempId, dto);
    }

    @PostMapping("/{tempId}/dl/verify")
    public void verifyDl(
            @PathVariable String tempId,
            @RequestBody DLVerificationResponseDTO dto
    ) {
        service.verifyDl(tempId, dto);
    }

    @PostMapping("/{tempId}/voter/upload")
    public void uploadVoter(
            @PathVariable String tempId,
            @RequestBody VoterIdOCRResponseDTO dto
    ) {
        service.processVoterOCR(tempId, dto);
    }

    @PostMapping("/{tempId}/voter/verify")
    public void verifyVoter(
            @PathVariable String tempId,
            @RequestBody VoterIdVerificationResponseDTO dto
    ) {
        service.verifyVoter(tempId, dto);
    }
}