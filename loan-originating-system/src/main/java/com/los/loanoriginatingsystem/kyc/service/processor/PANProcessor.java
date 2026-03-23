package com.los.loanoriginatingsystem.kyc.service.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.los.loanoriginatingsystem.integration.http.client.HttpCalloutService;
import com.los.loanoriginatingsystem.kyc.pan.dto.PanVerificationResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PANProcessor implements KYCProcessor {

    private final HttpCalloutService http;
    private final ObjectMapper objectMapper;

    @Override
    public String getType() {
        return "PAN";
    }

    @Override
    public Object process(byte[] file) {

        String response = http.execute(
                "pan",
                Map.of("pan", "ABCDE1234F"),
                null,
                null
        );

        try {
            return objectMapper.readValue(response, PanVerificationResponseDTO.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}