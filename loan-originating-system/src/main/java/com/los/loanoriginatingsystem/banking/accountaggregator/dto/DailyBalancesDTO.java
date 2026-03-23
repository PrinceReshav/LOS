package com.los.loanoriginatingsystem.banking.accountaggregator.dto;

import lombok.Data;
import java.util.List;

@Data
public class DailyBalancesDTO {

    private String month;

    private List<DailyBalanceDTO> dailyBalance;
}