package com.los.loanoriginatingsystem.banking.accountaggregator.dto;

import lombok.Data;
import java.util.List;

@Data
public class BankStatementResponseDTO {

    private String docId;
    private String status;
    private String message;
    private String periodStart;
    private String periodEnd;
    private String documents;

    private List<BankDataDTO> data;

    private String reportURL;
    private String reportData;
    private String reportFileName;

    private List<FileDetailsDTO> fileDetails;

    private boolean error;
}