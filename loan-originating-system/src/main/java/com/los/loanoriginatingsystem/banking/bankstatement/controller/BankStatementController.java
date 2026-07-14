/*
package com.los.loanoriginatingsystem.banking.bankstatement.controller;
 *

import com.los.loanoriginatingsystem.banking.bankstatement.service.BankStatementService;
import com.los.loanoriginatingsystem.banking.bankstatement.service.BankStatementUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/bank-statements")
@RequiredArgsConstructor
public class BankStatementController {

  //  private final BankStatementDocumentService documentService;
  //  private final BankStatementUpdateService updateService;
    private final BankStatementService bankStatementService;


    @GetMapping("/{applicationId}")
    public List<BankStatementDocDTO> getStatements(
            @PathVariable String applicationId) {

        return documentService.getBankStatementDocs(applicationId);
    }

    @GetMapping("/{documentId}")
    public Object getBankStatements(@PathVariable String documentId) {

        return bankStatementService.getBankStatements(documentId);
    }

    @PostMapping("/update")
    public String updateBankStatements(
            @RequestParam String documentId,
            @RequestParam String rowsData,
            @RequestParam Boolean isPrimary,
            @RequestParam BigDecimal allAverage) {

        return updateService.updateBankStatements(
                documentId,
                rowsData,
                isPrimary,
                allAverage
        );
    }
}
*/