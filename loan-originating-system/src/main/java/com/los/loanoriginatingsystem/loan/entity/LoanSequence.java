package com.los.loanoriginatingsystem.loan.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "loan_sequence")
@Data
public class LoanSequence {

    @Id
    private String name; // "LAN" or  "LA"

    @Column(nullable = false)
    private Long value;
}