package com.los.loanoriginatingsystem.lead.service;

import com.los.loanoriginatingsystem.lead.dto.CreateLeadRequest;
import com.los.loanoriginatingsystem.lead.entity.Lead;
import com.los.loanoriginatingsystem.lead.enums.LeadStatus;
import com.los.loanoriginatingsystem.lead.repository.LeadRepository;
import com.los.loanoriginatingsystem.temp.entity.TempLoanApplication;
import com.los.loanoriginatingsystem.temp.service.TempLoanApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeadService {

    private final LeadRepository leadRepository;
    private final LeadNumberService leadNumberService;
    private final TempLoanApplicationService tempService;

    // =====================================================
    // CREATE LEAD
    // =====================================================

    @Transactional
    public Lead createLead(
            CreateLeadRequest request
    ) {

        leadRepository
                .findByMobileNumber(
                        request.getMobile()
                )
                .ifPresent(existing -> {

                    throw new IllegalStateException(
                            "Lead already exists with mobile number : "
                                    + request.getMobile()
                    );
                });

        validateDates(
                request
        );

        Lead lead =
                new Lead();

        lead.setId(
                UUID.randomUUID().toString()
        );

        lead.setLeadNumber(
                leadNumberService.generateLeadNumber()
        );

        lead.setFirstName(
                request.getFirstName()
        );

        lead.setLastName(
                request.getLastName()
        );

        lead.setMobileNumber(
                request.getMobile()
        );

        lead.setGender(
                request.getGender().name()
        );

        lead.setLeadSource(
                request.getSource().name()
        );

        lead.setState(
                request.getState().name()
        );

        lead.setBusinessType(
                request.getBusinessType().name()
        );

        lead.setInterestLevel(
                request.getInterestLevel().name()
        );

        lead.setFollowUpDate(
                request.getFollowUpDate()
        );

        lead.setExpectedLoginDate(
                request.getExpectedLoginDate()
        );

        lead.setStatus(
                LeadStatus.NEW.name()
        );

        lead.setIsConverted(
                false
        );

        Lead saved =
                leadRepository.save(
                        lead
                );

        log.info(
                "Lead created : {}",
                saved.getLeadNumber()
        );

        return saved;
    }

    // =====================================================
    // GET
    // =====================================================

    @Transactional(readOnly = true)
    public List<Lead> getAllLeads() {

        return leadRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Lead getLead(
            String leadId
    ) {

        return leadRepository.findById(
                        leadId
                )
                .orElseThrow(
                        () -> new RuntimeException(
                                "Lead not found : "
                                        + leadId
                        )
                );
    }

    // =====================================================
    // UPDATE
    // =====================================================

    @Transactional
    public Lead updateLead(
            String leadId,
            CreateLeadRequest request
    ) {

        Lead lead =
                getLead(
                        leadId
                );

        validateDates(
                request
        );

        leadRepository
                .findByMobileNumber(
                        request.getMobile()
                )
                .ifPresent(existing -> {

                    if (
                            !existing.getId()
                                    .equals(leadId)
                    ) {

                        throw new IllegalStateException(
                                "Mobile number already used by another lead"
                        );
                    }
                });

        lead.setFirstName(
                request.getFirstName()
        );

        lead.setLastName(
                request.getLastName()
        );

        lead.setMobileNumber(
                request.getMobile()
        );

        lead.setGender(
                request.getGender().name()
        );

        lead.setLeadSource(
                request.getSource().name()
        );

        lead.setInterestLevel(
                request.getInterestLevel().name()
        );

        lead.setState(
                request.getState().name()
        );

        lead.setBusinessType(
                request.getBusinessType().name()
        );

        lead.setFollowUpDate(
                request.getFollowUpDate()
        );

        lead.setExpectedLoginDate(
                request.getExpectedLoginDate()
        );

        return leadRepository.save(
                lead
        );
    }

    // =====================================================
    // DELETE
    // =====================================================

    @Transactional
    public void deleteLead(
            String leadId
    ) {

        Lead lead =
                getLead(
                        leadId
                );

        if (
                Boolean.TRUE.equals(
                        lead.getIsConverted()
                )
        ) {

            throw new IllegalStateException(
                    "Converted lead cannot be deleted"
            );
        }

        leadRepository.delete(
                lead
        );
    }

    // =====================================================
    // START / RESUME
    // =====================================================

    @Transactional
    public TempLoanApplication startOrResume(
            String leadId,
            boolean isNew
    ) {

        Lead lead =
                getLead(
                        leadId
                );

        TempLoanApplication temp =
                tempService.createOrResume(
                        leadId,
                        isNew
                );

        lead.setTempLoanId(
                temp.getId()
        );

        lead.setStatus(
                LeadStatus.IN_PROGRESS.name()
        );

        leadRepository.save(
                lead
        );

        return temp;
    }

    // =====================================================
    // CONVERT LEAD
    // =====================================================

    @Transactional
    public void markConverted(
            String leadId,
            String loanApplicationId
    ) {

        if (
                loanApplicationId == null
                        ||
                        loanApplicationId.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "Loan Application Id cannot be null"
            );
        }

        Lead lead =
                getLead(
                        leadId
                );

        if (
                Boolean.TRUE.equals(
                        lead.getIsConverted()
                )
        ) {

            log.info(
                    "Lead already converted : {}",
                    leadId
            );

            return;
        }

        lead.setLoanApplicationId(
                loanApplicationId
        );

        lead.setTempLoanId(
                null
        );

        lead.setIsConverted(
                true
        );

        lead.setStatus(
                LeadStatus.CONVERTED.name()
        );

        leadRepository.save(
                lead
        );

        log.info(
                "Lead converted successfully. Lead={} Loan={}",
                leadId,
                loanApplicationId
        );
    }

    // =====================================================
    // VALIDATION
    // =====================================================

    private void validateDates(
            CreateLeadRequest request
    ) {

        if (
                request.getFollowUpDate() != null
                        &&
                        request.getFollowUpDate()
                                .isBefore(
                                        LocalDate.now()
                                )
        ) {

            throw new IllegalArgumentException(
                    "Follow-up date cannot be in past"
            );
        }

        if (
                request.getExpectedLoginDate() != null
                        &&
                        request.getExpectedLoginDate()
                                .isBefore(
                                        LocalDate.now()
                                )
        ) {

            throw new IllegalArgumentException(
                    "Expected login date cannot be in past"
            );
        }
    }
}