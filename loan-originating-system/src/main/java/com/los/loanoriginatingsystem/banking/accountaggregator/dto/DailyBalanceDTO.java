package com.los.loanoriginatingsystem.banking.accountaggregator.dto;

import lombok.Data;

@Data
public class DailyBalanceDTO {

    private Long dat;

    private Double openingBalance;

    private Double closingBalance;
}