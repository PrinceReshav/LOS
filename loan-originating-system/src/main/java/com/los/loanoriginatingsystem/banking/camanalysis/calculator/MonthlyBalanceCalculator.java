package com.los.loanoriginatingsystem.banking.camanalysis.calculator;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class MonthlyBalanceCalculator {

    public List<String> lastSixMonths() {

        List<String> months = new ArrayList<>();

        LocalDate now = LocalDate.now();

        for (int i = 6; i >= 1; i--) {

            LocalDate month = now.minusMonths(i);

            months.add(month.format(DateTimeFormatter.ofPattern("MMM-yyyy")));
        }

        return months;
    }
}