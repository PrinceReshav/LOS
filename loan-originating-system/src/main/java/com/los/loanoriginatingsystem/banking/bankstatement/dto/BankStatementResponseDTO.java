package com.los.loanoriginatingsystem.banking.bankstatement.dto;

import lombok.Data;

@Data
public class BankStatementResponseDTO {

    private String status;
    private String viewFile;
    private String documentPassword;

}