package com.los.loanoriginatingsystem.banking.camanalysis.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class BankAccountCAMData {

    private String ifscCode;
    private String bankName;
    private Boolean docExists;
    private String accountName;
    private String accountNumber;
    private String accountType;
    private BigDecimal average;
    private String bankFullName;
    private String branchAddress;

    private List<MonthDataDTO> rowData;
}