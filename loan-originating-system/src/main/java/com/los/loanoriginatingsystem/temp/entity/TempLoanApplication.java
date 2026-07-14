package com.los.loanoriginatingsystem.temp.entity;

import com.los.loanoriginatingsystem.temp.entity.embedded.*;
import com.los.loanoriginatingsystem.temp.enums.ApplicationStage;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;


@Entity
@Table(
        name = "temp_loan_application"
)
@Data
public class TempLoanApplication {

    @Id
    private String id;

    // =============================
    // RELATIONS
    // =============================

    @Column(
            name = "lead_id",
            nullable = false,
            unique = true
    )
    private String leadId;

    private Boolean isResume;

    // =============================
    // STEP 1 → LOAN DETAILS
    // =============================
    @Embedded
    private LoanDetails loanDetails = new LoanDetails();

    // =============================
    // STEP 2 → MOBILE OTP
    // =============================
    @Embedded
    private MobileDetails mobile = new MobileDetails();

    // =============================
    // STEP 3 → KYC
    // =============================
    @Embedded
    private AadhaarDetails aadhaar = new AadhaarDetails();
    @Enumerated(EnumType.STRING)
    private KycStatus aadhaarStatus = KycStatus.NOT_STARTED;

    @Embedded
    private PanDetails pan = new PanDetails();
    @Enumerated(EnumType.STRING)
    private KycStatus panStatus = KycStatus.NOT_STARTED;

    @Embedded
    private DlDetails dl = new DlDetails();
    @Enumerated(EnumType.STRING)
    private KycStatus dlStatus = KycStatus.NOT_STARTED;


    @Embedded
    private VoterDetails voter = new VoterDetails();
    @Enumerated(EnumType.STRING)
    private KycStatus voterStatus = KycStatus.NOT_STARTED;


    @Embedded
    private LivenessDetails liveness = new LivenessDetails();
    @Enumerated(EnumType.STRING)
    private KycStatus livenessStatus = KycStatus.NOT_STARTED;

    @Enumerated(EnumType.STRING)
    private ApplicationStage currentStage =
            ApplicationStage.LOAN_DETAILS;

    // =============================
    // STEP 4 → APPLICANT DETAILS
    // =============================
    @Embedded
    private ApplicantDetails applicant = new ApplicantDetails();

    // =============================
    // FLOW FLAGS
    // =============================
    private Boolean isKycCompleted;
    private Boolean isApplicantCompleted;

    private Boolean isSubmitted = false;
    private String submissionRef; // optional idempotency key
    private String submissionStatus; // INITIATED / PROCESSING / COMPLETED / FAILED / FAILED_FINAL
    private String failureReason; // optional



    private Integer retryCount = 0;

    @Column(name = "last_retry_at")
    private LocalDateTime lastRetryAt;

}