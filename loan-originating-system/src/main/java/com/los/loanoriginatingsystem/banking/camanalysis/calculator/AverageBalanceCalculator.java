package com.los.loanoriginatingsystem.banking.camanalysis.calculator;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class AverageBalanceCalculator {

    public BigDecimal calculateAverage(List<BigDecimal> balances) {

        if (balances.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal sum = balances.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return sum.divide(BigDecimal.valueOf(balances.size()), 2, RoundingMode.HALF_UP);
    }
}