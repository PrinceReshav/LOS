package com.los.loanoriginatingsystem.banking.accountaggregator.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RecurringTransactionDTO {

    private Integer id;

    private Long transactionDate;

    private String narration;

    private String paymentMode;

    private String paymentCategory;

    private String cheque;

    private BigDecimal amount;

    private String type;

    private Double openingBalance;

    private Double closingBalance;

    private String monthYear;

    private String name;

    private boolean ignorableTransaction;

    private boolean holiday;
}