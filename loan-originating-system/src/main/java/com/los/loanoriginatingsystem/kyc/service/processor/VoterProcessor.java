package com.los.loanoriginatingsystem.kyc.service.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.los.loanoriginatingsystem.integration.http.client.HttpCalloutService;
import com.los.loanoriginatingsystem.kyc.voterid.dto.VoterIdVerificationResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class VoterProcessor implements KYCProcessor {

    private final HttpCalloutService http;
    private final ObjectMapper objectMapper;

    @Override
    public String getType() {
        return "VOTER";
    }

    @Override
    public Object process(byte[] file) {

        String response = http.execute(
                "voter",
                Map.of("epic", "ABC123"),
                null,
                null
        );

        try {
            return objectMapper.readValue(response, VoterIdVerificationResponseDTO.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}