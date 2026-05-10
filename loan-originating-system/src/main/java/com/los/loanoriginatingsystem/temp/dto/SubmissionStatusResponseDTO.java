package com.los.loanoriginatingsystem.temp.dto;

import lombok.Data;

@Data
public class SubmissionStatusResponseDTO {

    private String submissionRef;
    private String status;
    private String loanId;
    private String message;
}