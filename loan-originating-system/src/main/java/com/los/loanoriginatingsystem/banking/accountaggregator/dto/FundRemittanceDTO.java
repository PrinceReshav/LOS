package com.los.loanoriginatingsystem.banking.accountaggregator.dto;


import lombok.Data;
import java.util.List;

@Data
public class FundRemittanceDTO {

    private String month;

    private String totalAmount;

    private List<RecurringTransactionDTO> transactions;
}