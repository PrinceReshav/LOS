package com.los.loanoriginatingsystem.banking.ifsc.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "bank_ifsc_detail")
@Data
public class BankIfscDetail {

    @Id
    @Column(name = "ifsc_code")
    private String name;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "branch_name")
    private String branchName;

    @Column(name = "branch_address")
    private String branchAddress;
}