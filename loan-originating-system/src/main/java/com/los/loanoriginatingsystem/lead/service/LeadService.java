package com.los.loanoriginatingsystem.lead.service;

import com.los.loanoriginatingsystem.lead.dto.CreateLeadRequest;
import com.los.loanoriginatingsystem.lead.entity.Lead;
import com.los.loanoriginatingsystem.lead.enums.LeadStatus;
import com.los.loanoriginatingsystem.lead.repository.LeadRepository;
import com.los.loanoriginatingsystem.temp.entity.TempLoanApplication;
import com.los.loanoriginatingsystem.temp.service.TempLoanApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LeadService {

    private final LeadRepository leadrepository;
    private final LeadNumberService numberService;
    private final TempLoanApplicationService tempService;

    // =============================
    // 🚀 CREATE LEAD
    // =============================
    public Lead createLead(CreateLeadRequest req) {

        Lead lead = new Lead();

        lead.setId(UUID.randomUUID().toString());
        lead.setLeadNumber(numberService.generateLeadNumber());

        lead.setFirstName(req.getFirstName());
        lead.setLastName(req.getLastName());
        lead.setMobileNumber(req.getMobile());

        lead.setGender(req.getGender().name());
        lead.setLeadSource(req.getSource().name());
        lead.setStatus(LeadStatus.NEW.name());
        lead.setInterestLevel(req.getInterestLevel().name());

        validateDates(req);

        lead.setFollowUpDate(req.getFollowUpDate());
        lead.setExpectedLoginDate(req.getExpectedLoginDate());

        return leadrepository.save(lead);
    }

    private void validateDates(CreateLeadRequest req) {

        if (req.getFollowUpDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Follow-up date cannot be in past");
        }

        if (req.getExpectedLoginDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Expected login date cannot be in past");
        }
    }

    // =============================
    // 🔁 START / RESUME APPLICATION
    // =============================
    @Transactional
    public TempLoanApplication startOrResume(String leadId, boolean isNew) {

        Lead lead = leadrepository.findById(leadId)
                .orElseThrow(() -> new RuntimeException("Lead not found"));

        TempLoanApplication temp =
                tempService.createOrResume(leadId, isNew);

        lead.setTempLoanId(temp.getId());
        lead.setStatus("IN_PROGRESS");

        leadrepository.save(lead);

        return temp;
    }

    // =============================
    // ✅ MARK CONVERTED
    // =============================
    public void markConverted(String leadId, String loanId) {

        Lead lead = leadrepository.findById(leadId)
                .orElseThrow(() -> new RuntimeException("Lead not found"));

        lead.setLoanApplicationId(loanId);
        lead.setIsConverted(true);
        lead.setStatus("CONVERTED");

        leadrepository.save(lead);
    }

}