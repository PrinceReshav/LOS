package com.los.loanoriginatingsystem.kyc.service.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.los.loanoriginatingsystem.integration.http.client.HttpCalloutService;
import com.los.loanoriginatingsystem.kyc.drivinglicense.dto.DLVerificationResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class DLProcessor implements KYCProcessor {

    private final HttpCalloutService http;
    private final ObjectMapper objectMapper;

    @Override
    public String getType() {
        return "DL";
    }

    @Override
    public Object process(byte[] file) {

        String response = http.execute(
                "dl",
                Map.of("license", "DL123"),
                null,
                null
        );

        try {
            return objectMapper.readValue(response, DLVerificationResponseDTO.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}