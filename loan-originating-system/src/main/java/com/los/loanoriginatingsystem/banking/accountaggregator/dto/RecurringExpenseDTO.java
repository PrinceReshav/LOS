package com.los.loanoriginatingsystem.banking.accountaggregator.dto;


import lombok.Data;
import java.util.List;

@Data
public class RecurringExpenseDTO {

    private List<RecurringTransactionDTO> recurringTransaction;
}