package com.los.loanoriginatingsystem.kyc.service.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.los.loanoriginatingsystem.integration.http.client.HttpCalloutService;
import com.los.loanoriginatingsystem.kyc.aadhaar.dto.AadhaarOCRResponseDTO;
import com.los.loanoriginatingsystem.kyc.aadhaar.dto.AadhaarVerificationResponseDTO;
import com.los.loanoriginatingsystem.kyc.mapper.AadhaarMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AadhaarProcessor implements KYCProcessor {

    private final HttpCalloutService http;
    private final ObjectMapper objectMapper;
    private final AadhaarMapper mapper;

    @Override
    public String getType() {
        return "AADHAAR";
    }

    @Override
    public Object process(byte[] file) {

        // OCR
        String ocrResponse = http.execute(
                "aadhaar-ocr",
                Map.of("client_id", "123"),
                null,
                Map.of("aadhaar.pdf", file)
        );

        AadhaarOCRResponseDTO ocrDto = parse(ocrResponse, AadhaarOCRResponseDTO.class);

        String aadhaarNumber = mapper.extractAadhaarNumber(ocrDto);

        // VERIFY
        String verifyResponse = http.execute(
                "aadhaar-verify",
                Map.of("aadhaarNumber", aadhaarNumber),
                null,
                null
        );

        return parse(verifyResponse, AadhaarVerificationResponseDTO.class);
    }

    private <T> T parse(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Parsing failed", e);
        }
    }
}