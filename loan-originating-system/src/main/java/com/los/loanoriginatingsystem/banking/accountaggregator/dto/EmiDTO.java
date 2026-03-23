package com.los.loanoriginatingsystem.banking.accountaggregator.dto;

import lombok.Data;

import java.util.List;

@Data
public class EmiDTO {

    private String commonEntity;

    private Double amount;

    private List<RecurringTransactionDTO> transactions;
}
