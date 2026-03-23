package com.los.loanoriginatingsystem.kyc.service;

import com.los.loanoriginatingsystem.kyc.service.processor.KYCProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KYCOrchestrationService {

    private final List<KYCProcessor> processors;

    public Object process(String type, byte[] file) {

        return processors.stream()
                .filter(p -> p.getType().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Unsupported KYC type"))
                .process(file);
    }
}