package com.los.loanoriginatingsystem.lead.controller;

import com.los.loanoriginatingsystem.lead.dto.CreateLeadRequest;
import com.los.loanoriginatingsystem.lead.entity.Lead;
import com.los.loanoriginatingsystem.lead.service.LeadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/lead")
@RequiredArgsConstructor
public class LeadController {

    private final LeadService service;

    @PostMapping("/create")
    public Lead create(@RequestBody CreateLeadRequest req) {
        return service.createLead(req);
    }
}
