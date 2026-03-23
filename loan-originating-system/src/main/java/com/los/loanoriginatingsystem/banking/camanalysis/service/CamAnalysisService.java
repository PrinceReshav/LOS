package com.los.loanoriginatingsystem.banking.camanalysis.service;

import com.los.loanoriginatingsystem.banking.accountaggregator.dto.BankStatementResponseDTO;
import com.los.loanoriginatingsystem.banking.camanalysis.dto.BankAccountCAMData;

import java.util.List;

public interface CamAnalysisService {

    List<BankAccountCAMData> processBankStatement(BankStatementResponseDTO response);

}