package com.los.loanoriginatingsystem.banking.accountaggregator.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreditCardSettlementsDTO {

    private String month;

    private Double totalAmount;

    private List<RecurringTransactionDTO> transactions;
}