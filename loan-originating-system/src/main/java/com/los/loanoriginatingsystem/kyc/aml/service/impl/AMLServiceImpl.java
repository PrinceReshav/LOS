package com.los.loanoriginatingsystem.kyc.aml.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.los.loanoriginatingsystem.kyc.aml.dto.AMLResponseDTO;
import com.los.loanoriginatingsystem.kyc.aml.service.AMLService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AMLServiceImpl implements AMLService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public AMLResponseDTO getAMLData(List<String> amlDataList) {

        String url = "AML_ENDPOINT";

        String response =
                restTemplate.postForObject(url, amlDataList, String.class);

        try {
            return objectMapper.readValue(response, AMLResponseDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AML response", e);
        }
    }
}