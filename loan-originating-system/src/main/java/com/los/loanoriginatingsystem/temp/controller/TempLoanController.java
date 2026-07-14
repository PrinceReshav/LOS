package com.los.loanoriginatingsystem.temp.controller;

import com.los.loanoriginatingsystem.temp.dto.*;
import com.los.loanoriginatingsystem.temp.entity.TempLoanApplication;
import com.los.loanoriginatingsystem.temp.enums.ApplicationStage;
import com.los.loanoriginatingsystem.temp.service.TempLoanApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/temp-loans")
@RequiredArgsConstructor
public class TempLoanController {

    private final TempLoanApplicationService service;

    @PostMapping("/start")
    public TempLoanResponseDTO createOrResume(
            @RequestBody CreateOrResumeTempRequestDTO request
    ) {

        TempLoanApplication temp =
                service.createOrResume(
                        request.getLeadId(),
                        Boolean.TRUE.equals(request.getIsNew())
                );

        return TempLoanResponseDTO.builder()
                .tempId(temp.getId())
                .leadId(temp.getLeadId())
                .currentStage(temp.getCurrentStage().name())
                .resume(temp.getIsResume())
                .build();
    }


    @PostMapping("/{tempId}/previous-stage")
    public StageResponseDTO previousStage(
            @PathVariable String tempId
    ) {

        service.moveToPreviousStage(tempId);

        TempLoanApplication temp =
                service.getById(tempId);

        return StageResponseDTO.builder()
                .tempId(temp.getId())
                .currentStage(
                        temp.getCurrentStage().name()
                )
                .message(
                        "Moved to previous stage successfully"
                )
                .build();
    }

    @PostMapping("/{tempId}/loan-details")
    public StageResponseDTO saveLoanDetails(
            @PathVariable String tempId,
            @RequestBody SaveLoanDetailsRequestDTO request
    ) {

        service.saveLoanDetails(
                tempId,
                request.getLoanPurpose(),
                request.getLoanType(),
                request.getLoanProductId(),
                request.getLoanScheme(),
                request.getRequestedAmount(),
                request.getTenureMonths()
        );

        return StageResponseDTO.builder()
                .tempId(tempId)
                .currentStage(
                        ApplicationStage.MOBILE_VERIFICATION.name()
                )
                .message("Loan Details Saved Successfully")
                .build();
    }

    @PostMapping("/{tempId}/aadhaar")
    public StageResponseDTO saveAadhaar(
            @PathVariable String tempId,
            @RequestBody SaveAadhaarRequestDTO request
    ) {

        service.saveAadhaar(
                tempId,
                request
        );

        return StageResponseDTO.builder()
                .tempId(tempId)
                .currentStage(
                        ApplicationStage.PAN_VERIFICATION.name()
                )
                .message(
                        "Aadhaar Verified Successfully"
                )
                .build();
    }

    @GetMapping("/resume/{leadId}")
    public ResumeTempApplicationResponseDTO resume(
            @PathVariable String leadId
    ) {

        return service.getResumeData(
                leadId
        );
    }

    @GetMapping("/{tempId}")
    public TempLoanApplication getTempApplication(
            @PathVariable String tempId
    ) {

        return service.getTempApplication(
                tempId
        );
    }

    @PostMapping("/{tempId}/mobile/send-otp")
    public StageResponseDTO sendOtp(
            @PathVariable String tempId,
            @RequestBody SendOtpRequestDTO request
    ) {

        service.sendOtp(
                tempId,
                request.getMobile()
        );

        return StageResponseDTO.builder()
                .tempId(tempId)
                .currentStage(
                        ApplicationStage.MOBILE_VERIFICATION.name()
                )
                .message("OTP Sent Successfully")
                .build();
    }

    @PostMapping("/{tempId}/mobile/verify-otp")
    public StageResponseDTO verifyOtp(
            @PathVariable String tempId,
            @RequestBody VerifyOtpRequestDTO request
    ) {

        service.verifyOtp(
                tempId,
                request.getOtp()
        );

        return StageResponseDTO.builder()
                .tempId(tempId)
                .currentStage(
                        ApplicationStage.AADHAAR_VERIFICATION.name()
                )
                .message("Mobile Verified Successfully")
                .build();
    }

    @PostMapping("/{tempId}/submit")
    public StageResponseDTO submit(
            @PathVariable String tempId
    ) {

        service.submitApplication(tempId);

        return StageResponseDTO.builder()
                .tempId(tempId)
                .currentStage(
                        ApplicationStage.SUBMITTED.name()
                )
                .message("Application Submitted Successfully")
                .build();
    }

    @PostMapping("/{tempId}/pan")
    public StageResponseDTO savePan(
            @PathVariable String tempId,
            @RequestBody SavePanRequestDTO request
    ) {

        service.savePan(
                tempId,
                request
        );

        return StageResponseDTO.builder()
                .tempId(tempId)
                .currentStage(
                        ApplicationStage.APPLICANT_DETAILS.name()
                )
                .message(
                        "PAN Verified Successfully"
                )
                .build();
    }

    @PostMapping("/{tempId}/applicant")
    public StageResponseDTO saveApplicant(
            @PathVariable String tempId,
            @RequestBody SaveApplicantDetailsRequestDTO request
    ) {

        service.saveApplicant(
                tempId,
                request
        );

        return StageResponseDTO.builder()
                .tempId(tempId)
                .currentStage(
                        ApplicationStage.REVIEW.name()
                )
                .message(
                        "Applicant Details Saved Successfully"
                )
                .build();
    }

    @PostMapping("/{tempId}/applicant/complete")
    public StageResponseDTO completeApplicant(
            @PathVariable String tempId
    ) {

        service.completeApplicant(tempId);

        return StageResponseDTO.builder()
                .tempId(tempId)
                .currentStage(
                        ApplicationStage.REVIEW.name()
                )
                .message(
                        "Applicant Details Completed"
                )
                .build();
    }


}
