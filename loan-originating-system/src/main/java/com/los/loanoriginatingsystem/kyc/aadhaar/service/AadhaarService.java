package com.los.loanoriginatingsystem.kyc.aadhaar.service;

import com.los.loanoriginatingsystem.kyc.aadhaar.dto.AadhaarOCRResponseDTO;
import com.los.loanoriginatingsystem.kyc.aadhaar.dto.AadhaarVerificationResponseDTO;

public interface AadhaarService {

    AadhaarOCRResponseDTO parseOCRResponse(String json);

    AadhaarVerificationResponseDTO parseVerificationResponse(String json);
}