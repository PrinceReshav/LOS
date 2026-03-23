package com.los.loanoriginatingsystem.banking.camanalysis.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "average_bank_balance")
@Data
public class AverageBankBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loan_application_id")
    private String loanApplicationId;

    @Column(name = "document_id")
    private String documentId;

    @Column(name = "scheme_type")
    private String schemeType;

    @Column(name = "rows_data", columnDefinition = "TEXT")
    private String rowsData;

    @Column(name = "average_bank_balance")
    private BigDecimal averageBankBalance;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLoanApplicationId() {
        return loanApplicationId;
    }

    public void setLoanApplicationId(String loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getSchemeType() {
        return schemeType;
    }

    public void setSchemeType(String schemeType) {
        this.schemeType = schemeType;
    }

    public String getRowsData() {
        return rowsData;
    }

    public void setRowsData(String rowsData) {
        this.rowsData = rowsData;
    }

    public BigDecimal getAverageBankBalance() {
        return averageBankBalance;
    }

    public void setAverageBankBalance(BigDecimal averageBankBalance) {
        this.averageBankBalance = averageBankBalance;
    }


}