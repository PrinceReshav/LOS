package com.los.loanoriginatingsystem.loan.service;

import com.los.loanoriginatingsystem.loan.entity.LoanApplication;
import com.los.loanoriginatingsystem.loan.repository.LoanApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class LoanApplicationService {

    private final LoanApplicationRepository repository;

    public String saveBankStatementInLA(
            String applicationId,
            Map<String, Object> bankData) {

        updateBankDetails(applicationId, bankData);

        return "success";
    }

    public void saveBankStatementDetails(
            String applicationId,
            Map<String, Object> bankData) {

        updateBankDetails(applicationId, bankData);
    }

    private void updateBankDetails(
            String applicationId,
            Map<String, Object> bankData) {

        LoanApplication la =
                repository.findById(applicationId).orElseThrow();

        la.setBankName((String) bankData.get("Bank_Name"));
        la.setAccountHolderName((String) bankData.get("Account_Holder_Name"));
        la.setAccountNumber((String) bankData.get("Account_Number"));
        la.setIfscCode((String) bankData.get("IFSC_Code"));
        la.setAccountType((String) bankData.get("Account_Type"));
        la.setBranchNameAddress((String) bankData.get("Branch_Name_Address"));
        la.setAccountHolderType((String) bankData.get("Account_Holder_Type"));

        repository.save(la);
    }
}