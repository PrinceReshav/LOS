package com.los.loanoriginatingsystem.temp.controller;


import com.los.loanoriginatingsystem.temp.dto.SubmissionStatusResponseDTO;
import com.los.loanoriginatingsystem.temp.service.status.SubmissionStatusService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/submission")
@RequiredArgsConstructor
public class SubmissionStatusController {

    private final SubmissionStatusService service;

    @GetMapping("/status/{submissionRef}")
    public SubmissionStatusResponseDTO getStatus(
            @PathVariable String submissionRef
    ) {
        return service.getStatus(submissionRef);
    }
}