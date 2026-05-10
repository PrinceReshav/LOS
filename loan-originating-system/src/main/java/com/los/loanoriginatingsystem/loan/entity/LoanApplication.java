package com.los.loanoriginatingsystem.loan.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "loan_application")
@Data
public class LoanApplication {

    @Id
    private String id;

    @Column(name = "loan_account_number", unique = true, nullable = false)
    private String loanAccountNumber;

    @Column(name="account_aggregator_file_no")
    private String accountAggregatorFileNo;

    @Column(name="account_aggregator_status")
    private String accountAggregatorStatus;

    @Column(name="account_aggregator_url")
    private String accountAggregatorUrl;

    @Column(name="average_bank_balance")
    private BigDecimal averageBankBalance;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "account_holder_name")
    private String accountHolderName;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "ifsc_code")
    private String ifscCode;

    @Column(name = "account_type")
    private String accountType;

    @Column(name = "branch_name_address")
    private String branchNameAddress;

    @Column(name = "account_holder_type")
    private String accountHolderType;

    @Column(name = "annualised_turnover")
    private BigDecimal annualisedTurnover;

    @Column(name = "temp_id", unique = true)
    private String tempId;
}