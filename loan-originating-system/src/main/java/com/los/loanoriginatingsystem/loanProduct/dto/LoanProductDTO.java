package com.los.loanoriginatingsystem.loanProduct.dto;


import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class LoanProductDTO {

    private String id;
    private String name;
    private String productCode;
    private String loanScheme;

    private BigDecimal minAmount;
    private BigDecimal maxAmount;

    private List<String> applicantDocs;
    private List<String> applicationDocs;
}