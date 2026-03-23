package com.los.loanoriginatingsystem.banking.accountaggregator.dto;

import lombok.Data;

@Data
public class AACallbackDTO {

    private String docId;
    private String requestId;
    private String status;
    private String reportFileName;
    private String endTime;
    private String message;
    private String fileNo;
}