package com.los.loanoriginatingsystem.applicant.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "loan_applicant")
@Data
public class LoanApplicant {

    @Id
    private String id;

    private String loanApplicationId;

    @Column(name = "applicant_number", unique = true, nullable = false)
    private String applicantNumber;


    // Aadhaar
    private String aadhaarNumber;
    private String aadhaarName;
    private String aadhaarDob;
    private String aadhaarGender;

    // PAN
    private String panNumber;
    private String panName;

    // Mobile
    private String mobileNumber;

    // Address
    private String house;
    private String street;
    private String district;
    private String state;
    private String pincode;
}