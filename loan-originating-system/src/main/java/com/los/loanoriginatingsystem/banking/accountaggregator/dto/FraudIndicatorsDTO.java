package com.los.loanoriginatingsystem.banking.accountaggregator.dto;

import lombok.Data;
import java.util.List;

@Data
public class FraudIndicatorsDTO {

    private String name;

    private String description;

    private List<RecurringTransactionDTO> transactions;
}