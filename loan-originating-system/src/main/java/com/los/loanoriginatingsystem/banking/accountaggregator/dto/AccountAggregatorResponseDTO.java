package com.los.loanoriginatingsystem.banking.accountaggregator.dto;

import lombok.Data;

@Data
public class AccountAggregatorResponseDTO {

    private String requestId;
    private String tempUrl;
    private Long validFrom;
    private Long validTo;
    private String emailId;
    private String userId;
    private Integer active;
    private String status;
    private String sessionKey;
    private String fileNo;
    private String name;
    private String contactNo;
    private String accountType;
    private String docId;
}