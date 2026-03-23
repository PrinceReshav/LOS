package com.los.loanoriginatingsystem.banking.accountaggregator.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AnalysisDataDTO {

    private String month;

    private Integer noOfCreditTransactions;

    private BigDecimal creditTransactionsAmount;

    private Integer noOfDebitTransactions;

    private BigDecimal debitTransactionsAmount;

    private Integer noOfNetCreditTransactions;

    private BigDecimal netCreditTransactionsAmount;

    private Integer noOfNetDebitTransactions;

    private BigDecimal netDebitTransactionsAmount;

    private Double minimumEODBalance;

    private Double maximumEODBalance;

    private Double averageEODBalance;

}
