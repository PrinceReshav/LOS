package com.los.loanoriginatingsystem.loanProduct.config.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "loan_tenure_config")
@Data
public class LoanTenureConfig {

    @Id
    private String id;

    private String name;

    private BigDecimal minAmount;
    private BigDecimal maxAmount;

    private Integer minTenure;
    private Integer maxTenure;
}
