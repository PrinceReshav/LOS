package com.los.loanoriginatingsystem.temp.controller;

import com.los.loanoriginatingsystem.temp.dto.TempApplicationResumeDTO;
import com.los.loanoriginatingsystem.temp.entity.TempLoanApplication;
import com.los.loanoriginatingsystem.temp.repository.TempLoanApplicationRepository;
import com.los.loanoriginatingsystem.temp.service.TempLoanApplicationService;
import com.los.loanoriginatingsystem.temp.service.kyc.KycTempService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/temp/kyc")
@RequiredArgsConstructor
public class TempLoanApplicationController {

    private final KycTempService kycService;
    private final TempLoanApplicationRepository tempLoanApplicationRepository;

    @GetMapping("/{leadId}/resume")
    public TempApplicationResumeDTO resume(
            @PathVariable String leadId
    ) {

        TempLoanApplication temp =
                tempLoanApplicationRepository.findByLeadId(leadId)
                        .orElseThrow();

        return TempApplicationResumeDTO
                .builder()
                .tempId(temp.getId())
                .leadId(temp.getLeadId())
                .isResume(temp.getIsResume())
                .isSubmitted(temp.getIsSubmitted())
                .currentStage(
                        temp.getCurrentStage()
                )
                .build();
    }

    @PostMapping("/aadhaar/otp")
    public void sendOtp(@RequestParam String tempId) {
        // call service with DTO
    }

    @PostMapping("/pan/verify")
    public void verifyPan(@RequestParam String tempId) {
        // call service
    }

    @PostMapping("/liveness")
    public void liveness(@RequestParam String tempId,
                         @RequestParam String base64) {

        kycService.saveLiveness(tempId, base64);
    }
}