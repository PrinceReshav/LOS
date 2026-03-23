package com.los.loanoriginatingsystem.banking.accountaggregator.dto;

import lombok.Data;
import java.util.List;

@Data
public class ChequeBouncesDTO {

    private String month;

    private List<RecurringTransactionDTO> transactions;
}