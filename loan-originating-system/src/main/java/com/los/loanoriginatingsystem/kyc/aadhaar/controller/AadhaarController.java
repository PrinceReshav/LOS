package com.los.loanoriginatingsystem.kyc.aadhaar.controller;

import com.los.loanoriginatingsystem.kyc.aadhaar.dto.AadhaarVerificationResponseDTO;
import com.los.loanoriginatingsystem.kyc.aadhaar.service.AadhaarService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kyc/aadhaar")
@RequiredArgsConstructor
public class AadhaarController {

    private final AadhaarService aadhaarService;

    @PostMapping("/verify")
    public AadhaarVerificationResponseDTO verify(@RequestBody String jsonResponse) {

        return aadhaarService.parseVerificationResponse(jsonResponse);
    }
}