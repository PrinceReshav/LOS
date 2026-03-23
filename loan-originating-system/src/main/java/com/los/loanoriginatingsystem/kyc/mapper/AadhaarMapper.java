package com.los.loanoriginatingsystem.kyc.mapper;

import com.los.loanoriginatingsystem.kyc.aadhaar.dto.AadhaarOCRResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class AadhaarMapper {

    public String extractAadhaarNumber(AadhaarOCRResponseDTO dto) {

        return dto.getData()
                .getOcrFields()
                .stream()
                .findFirst()
                .map(f -> f.getAadhaarNumber().getValue())
                .orElseThrow();
    }
}