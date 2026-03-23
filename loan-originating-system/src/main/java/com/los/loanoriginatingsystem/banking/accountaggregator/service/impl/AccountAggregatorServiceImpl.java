package com.los.loanoriginatingsystem.banking.accountaggregator.service.impl;

import com.los.loanoriginatingsystem.banking.accountaggregator.dto.AccountAggregatorResponseDTO;
import com.los.loanoriginatingsystem.banking.accountaggregator.dto.BankStatementResponseDTO;
import com.los.loanoriginatingsystem.banking.accountaggregator.service.AccountAggregatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountAggregatorServiceImpl implements AccountAggregatorService {

    private final RestTemplate restTemplate;

    @Value("${aa.base-url}")
    private String baseUrl;

    @Value("${aa.bank-statement-url}")
    private String bankStatementUrl;

    @Value("${aa.token}")
    private String token;

    @Override
    public AccountAggregatorResponseDTO initiateAA(List<String> requestBody, String applicationId) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("auth-Token", token);

        HttpEntity<List<String>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<AccountAggregatorResponseDTO> response =
                restTemplate.exchange(
                        baseUrl,
                        HttpMethod.POST,
                        entity,
                        AccountAggregatorResponseDTO.class
                );

        return response.getBody();

    }

    @Override
    public BankStatementResponseDTO downloadBankStatement(String documentId) {

        return restTemplate.getForObject(
                bankStatementUrl + "/" + documentId,
                BankStatementResponseDTO.class
        );
    }
}