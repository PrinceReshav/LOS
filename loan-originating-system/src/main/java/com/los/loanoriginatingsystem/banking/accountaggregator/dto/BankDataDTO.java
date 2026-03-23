package com.los.loanoriginatingsystem.banking.accountaggregator.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class BankDataDTO {

    private String bankName;
    private String bankFullName;
    private String accountNumber;
    private String accountName;
    private String ifscCode;
    private String accountType;

    private String accountOpeningDate;
    private String productType;
    private String periodStart;
    private String periodEnd;

    private String address;
    private String email;
    private String mobileNumber;
    private String panNumber;

    private Integer monthsEvaluated;

    private String documentType;
    private String lastCustomerTransactionDate;

    private CamAnalysisDTO camAnalysisData;

    private List<AnalysisDataDTO> analysisData;

    private List<RecurringTransactionDTO> transactions;

    private List<FundRemittanceDTO> fundRemittance;

    private List<FundReceivedDTO> fundReceived;

    private List<DailyBalancesDTO> dailyBalances;

    private List<FraudIndicatorsDTO> fraudIndicators;

    private BigDecimal fraudScore;

    private List<SalaryDTO> salary;

    private List<EmiDTO> emi;

    private List<ChequeBouncesDTO> chequeBounces;

    private BigDecimal billPayment;

    private BigDecimal income;

    private String payments;

    private List<RecurringExpenseDTO> recurringIncome;

    private List<RecurringExpenseDTO> recurringExpense;

    private String gstr3bDetail;

    private String matchCibil;

    private String unmatchCibil;

    private List<CreditCardSettlementsDTO> creditCardSettlement;

    private String formAS;

    private String itr1;

    private String itr;

    private String GST;

    private String observation;

    private List<Map<String,String>> top10Buyers;

    private List<Map<String,String>> top10Suppliers;

    private String creditCardDetails;

    private String expenses;

    private String tradeCredits;

    private String tradeDebits;

    private String nonTradeCredits;

    private String nonTradeDebits;

    private String salarySlips;

    private String mcaReportDatas;

    private String bsPnLDatas;

    private String bsaCreditData;
}