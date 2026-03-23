package com.los.loanoriginatingsystem.kyc.aadhaar.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.los.loanoriginatingsystem.kyc.aadhaar.dto.AadhaarOCRResponseDTO;
import com.los.loanoriginatingsystem.kyc.aadhaar.dto.AadhaarVerificationResponseDTO;
import com.los.loanoriginatingsystem.kyc.aadhaar.service.AadhaarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AadhaarServiceImpl implements AadhaarService {

    private final ObjectMapper objectMapper;

    @Override
    public AadhaarOCRResponseDTO parseOCRResponse(String json) {

        try {
            return objectMapper.readValue(json, AadhaarOCRResponseDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Aadhaar OCR response", e);
        }
    }

    @Override
    public AadhaarVerificationResponseDTO parseVerificationResponse(String json) {

        try {
            return objectMapper.readValue(json, AadhaarVerificationResponseDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Aadhaar verification response", e);
        }
    }
}