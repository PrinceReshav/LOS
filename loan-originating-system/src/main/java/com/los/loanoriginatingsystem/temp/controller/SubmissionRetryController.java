package com.los.loanoriginatingsystem.temp.controller;

import com.los.loanoriginatingsystem.temp.service.status.SubmissionRetryService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/submission")
@RequiredArgsConstructor
public class SubmissionRetryController {

    private final SubmissionRetryService service;

    @PostMapping("/retry/{submissionRef}")
    public String retry(@PathVariable String submissionRef) {

        return service.retry(submissionRef);
    }
}