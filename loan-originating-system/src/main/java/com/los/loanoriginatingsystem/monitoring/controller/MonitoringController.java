package com.los.loanoriginatingsystem.monitoring.controller;

import com.los.loanoriginatingsystem.monitoring.service.MonitoringService;
import com.los.loanoriginatingsystem.temp.entity.TempLoanApplication;
import com.los.loanoriginatingsystem.temp.repository.TempLoanApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/monitoring")
@RequiredArgsConstructor
public class MonitoringController {

    private final MonitoringService service;
    private final TempLoanApplicationRepository repository;

    @GetMapping("/summary")
    public Map<String, Object> summary() {

        return Map.of(
                "total", service.total(),
                "completed", service.completed(),
                "processing", service.processing(),
                "failed", service.failed(),
                "failedFinal", service.failedFinal()
        );
    }

    @GetMapping("/dlq")
    public List<TempLoanApplication> dlq() {
        return repository.findBySubmissionStatus("FAILED_FINAL");
    }
}