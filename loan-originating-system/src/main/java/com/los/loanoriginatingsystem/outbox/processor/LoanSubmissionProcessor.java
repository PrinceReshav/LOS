package com.los.loanoriginatingsystem.outbox.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.los.loanoriginatingsystem.applicant.entity.LoanApplicant;
import com.los.loanoriginatingsystem.applicant.repository.LoanApplicantRepository;
import com.los.loanoriginatingsystem.audit.service.AuditService;
import com.los.loanoriginatingsystem.document.entity.Document;
import com.los.loanoriginatingsystem.document.repository.DocumentRepository;
import com.los.loanoriginatingsystem.lead.service.LeadService;
import com.los.loanoriginatingsystem.loan.entity.LoanApplication;
import com.los.loanoriginatingsystem.loan.repository.LoanApplicationRepository;
import com.los.loanoriginatingsystem.loan.service.LoanNumberService;
import com.los.loanoriginatingsystem.notification.service.AlertService;
import com.los.loanoriginatingsystem.outbox.entity.OutboxEvent;
import com.los.loanoriginatingsystem.outbox.service.OutboxService;
import com.los.loanoriginatingsystem.temp.entity.TempLoanApplication;

import com.los.loanoriginatingsystem.temp.repository.TempLoanApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanSubmissionProcessor {

    private final LoanApplicationRepository loanRepo;
    private final LoanApplicantRepository applicantRepo;
    private final DocumentRepository documentRepo;
    private final LeadService leadService;
    private final LoanNumberService loanNumberService;
    private final ObjectMapper objectMapper;
    private final TempLoanApplicationRepository tempRepo;
    private final OutboxService  outboxService;
    private final AlertService alertService;
    private final AuditService auditService;




    @Transactional
    public void process(OutboxEvent event) {

        String eventId = event.getId();
        String tempId = null;

        try {

            // =============================
            // 🔍 PARSE PAYLOAD
            // =============================
            TempLoanApplication temp =
                    objectMapper.readValue(event.getPayload(), TempLoanApplication.class);

            tempId = temp.getId();

            log.info("Processing loan submission eventId={} tempId={}", eventId, tempId);

            // =============================
            // 🔄 MARK PROCESSING
            // =============================
            TempLoanApplication dbTemp =
                    tempRepo.findById(tempId)
                            .orElseThrow();

            dbTemp.setSubmissionStatus("PROCESSING");
            tempRepo.save(dbTemp);



            // =============================
            // 🔒 IDEMPOTENCY CHECK (CRITICAL)
            // =============================
            if (loanRepo.existsByTempId(tempId)) {
                log.warn("Duplicate processing avoided for tempId={}", tempId);
                return;
            }

            // =============================
            // 🆔 GENERATE LAN
            // =============================
            String lan = loanNumberService.generateLAN();

            // =============================
            // 🏦 CREATE LOAN
            // =============================
            LoanApplication loan = new LoanApplication();

            loan.setId(UUID.randomUUID().toString());
            loan.setLoanAccountNumber(lan);
            loan.setTempId(tempId); // 🔥 IMPORTANT

            loanRepo.save(loan);

            // 🔥 AUDIT (CRITICAL)
            auditService.log(
                    "LOAN",
                    loan.getId(),
                    "CREATE",
                    "INITIATED",
                    "COMPLETED",
                    "Loan successfully created"
            );

            // =============================
            // 👤 CREATE APPLICANT
            // =============================
            LoanApplicant applicant = new LoanApplicant();

            applicant.setId(UUID.randomUUID().toString());
            applicant.setLoanApplicationId(loan.getId());

            // Defensive mapping
            if (temp.getAadhaar() != null) {
                applicant.setAadhaarNumber(temp.getAadhaar().getVerifiedAadhaarNumber());
            }

            if (temp.getPan() != null) {
                applicant.setPanNumber(temp.getPan().getVerifiedPanNumber());
            }

            if (temp.getMobile() != null) {
                applicant.setMobileNumber(temp.getMobile().getMobileNumber());
            }

            applicantRepo.save(applicant);

            // =============================
            // 📄 DOCUMENT LINKING
            // =============================
            List<Document> docs = documentRepo.findByTempLoanId(tempId);

            for (Document doc : docs) {

                doc.setTempLoanId(null);
                doc.setLoanApplicationId(loan.getId());

                if (doc.getKycType() != null) {
                    doc.setLoanApplicantId(applicant.getId());
                }
            }

            documentRepo.saveAll(docs);

            // =============================
            // 🔗 UPDATE LEAD
            // =============================
            if (temp.getLeadId() != null) {
                leadService.markConverted(temp.getLeadId(), loan.getId());
            }

            // =============================
            // ✅ MARK COMPLETED
            // =============================
            dbTemp.setSubmissionStatus("COMPLETED");
            tempRepo.save(dbTemp);

            // =============================
            // ✅ SUCCESS LOG
            // =============================
            log.info("Loan created successfully tempId={} loanId={} LAN={}",
                    tempId, loan.getId(), lan);

        }
        catch (Exception e) {

            log.error("❌ Loan submission failed eventId={}", eventId, e);

            if (tempId != null) {

                TempLoanApplication dbTemp =
                        tempRepo.findById(tempId).orElse(null);

                if (dbTemp != null) {

                    int retry = dbTemp.getRetryCount() == null ? 0 : dbTemp.getRetryCount();

                    if (retry >= 3) {

                        // =============================
                        // 💀 FINAL FAILURE → DLQ
                        // =============================
                        dbTemp.setSubmissionStatus("FAILED_FINAL");

                        // 🔥 PUSH TO DLQ
                        outboxService.saveEvent(
                                "DLQ",
                                dbTemp.getId(),
                                "LOAN_FAILED",
                                dbTemp
                        );

                        // 🔔 ALERT
                        alertService.sendFailureAlert(dbTemp, e);

                    } else {

                        dbTemp.setSubmissionStatus("FAILED");
                    }

                    dbTemp.setFailureReason(e.getMessage());
                    tempRepo.save(dbTemp);
                }
            }

            throw new RuntimeException("Loan processing failed", e);
        }
    }
}