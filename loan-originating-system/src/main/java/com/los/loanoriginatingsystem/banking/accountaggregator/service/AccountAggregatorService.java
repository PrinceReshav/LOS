package com.los.loanoriginatingsystem.banking.accountaggregator.service;

import com.los.loanoriginatingsystem.banking.accountaggregator.dto.AccountAggregatorResponseDTO;
import com.los.loanoriginatingsystem.banking.accountaggregator.dto.BankStatementResponseDTO;

import java.util.List;

public interface AccountAggregatorService {

    AccountAggregatorResponseDTO initiateAA(List<String> requestBody, String applicationId);


    BankStatementResponseDTO downloadBankStatement(String documentId);
}