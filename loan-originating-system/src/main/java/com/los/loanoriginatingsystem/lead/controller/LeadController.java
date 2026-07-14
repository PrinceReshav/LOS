package com.los.loanoriginatingsystem.lead.controller;

import com.los.loanoriginatingsystem.lead.dto.CreateLeadRequest;
import com.los.loanoriginatingsystem.lead.entity.Lead;
import com.los.loanoriginatingsystem.lead.service.LeadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/lead")
@RequiredArgsConstructor
public class LeadController {

    private final LeadService service;


    @GetMapping
    public List<Lead> getAllLeads() {
        return service.getAllLeads();
    }

    @GetMapping("/{leadId}")
    public Lead getLead(
            @PathVariable String leadId
    ) {
        return service.getLead(leadId);
    }

    @PostMapping("/create")
    public Lead createLead(
            @RequestBody CreateLeadRequest request
    ) {
        System.out.println("INSIDE CREATE LEAD");
        return service.createLead(request);
    }

    @PutMapping("/{leadId}")
    public Lead updateLead(
            @PathVariable String leadId,
            @RequestBody CreateLeadRequest request
    ) {
        return service.updateLead(
                leadId,
                request
        );
    }

    @DeleteMapping("/{leadId}")
    public void deleteLead(
            @PathVariable String leadId
    ) {
        service.deleteLead(leadId);
    }
}
