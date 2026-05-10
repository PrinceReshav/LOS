package com.los.loanoriginatingsystem.temp.controller;

import com.los.loanoriginatingsystem.temp.service.kyc.KycTempService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/temp/kyc")
@RequiredArgsConstructor
public class TempLoanApplicationController {

    private final KycTempService kycService;

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