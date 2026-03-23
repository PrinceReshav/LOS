package com.los.loanoriginatingsystem.banking.accountaggregator.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SalaryDTO {

    private String month;

    private BigDecimal totalSalary;

    private List<RecurringTransactionDTO> transactions;
}