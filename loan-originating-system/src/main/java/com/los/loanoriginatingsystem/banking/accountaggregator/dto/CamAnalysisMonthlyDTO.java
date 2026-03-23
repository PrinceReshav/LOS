package com.los.loanoriginatingsystem.banking.accountaggregator.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

@Data
public class CamAnalysisMonthlyDTO {

    private String month;

    private Integer noOfCredit;

    private BigDecimal grossCreditAmount;

    private Integer noOfNetCredit;

    private BigDecimal netCreditAmount;

    private BigDecimal internalCredits;

    private Integer noOfDebit;

    private BigDecimal grossDebitAmount;

    private Integer noOfNetDebit;

    private BigDecimal netDebitAmount;

    private BigDecimal internalDebit;

    private Integer noOfInwardReturn;

    private BigDecimal inwardReturn;

    private Integer noOfOutwardReturn;

    private BigDecimal outwardReturn;

    private BigDecimal loanDisbursal;

    private Map<String,String> customDayBalances;

    private Integer maxContinuousOverdrawings;

    private BigDecimal monthlyAvgInclOdCcLimit;

    private Integer instancesOfOverdrawings;

    private BigDecimal maxOverdrawnAmount;

    private Integer maxInterestServicingDays;

    private Double overallAveragePositiveNegativeEODBalance;

    private BigDecimal averageUtilisedNegativeEODBalance;

    private BigDecimal averageUnutilisedCustomDayBalances;

    private Double averageUtilisedCustomDayBalances;

    private BigDecimal minBalance;

    private BigDecimal maxBalance;
}