package com.los.loanoriginatingsystem.kyc.controller;

import com.los.loanoriginatingsystem.kyc.service.KYCOrchestrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/kyc")
@RequiredArgsConstructor
public class KYCController {

    private final KYCOrchestrationService service;

    @PostMapping("/{type}")
    public Object process(
            @PathVariable String type,
            @RequestParam("file") MultipartFile file) throws Exception {

        return service.process(type, file.getBytes());
    }
}