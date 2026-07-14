package com.los.loanoriginatingsystem.lead.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "lead")
@Data
public class Lead {

    @Id
    private String id;

    @Column(unique = true, nullable = false)
    private String leadNumber;

    // =============================
    // PERSONAL DETAILS
    // =============================
    private String firstName;
    private String middleName;
    private String lastName;

    private String gender;

    private String email;

    private String mobileNumber;

    // =============================
    // ADDRESS (RESIDENTIAL)
    // =============================
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String landmark;
    private String pincode;
    private String city;
    private String state;

    // =============================
    // BUSINESS DETAILS
    // =============================
    private String businessName;
    private String businessType;

    private String businessAddressLine1;
    private String businessAddressLine2;
    private String businessAddressLine3;
    private String businessLandmark;
    private String businessPincode;
    private String businessCity;
    private String businessState;

    // =============================
    // FOLLOW-UP
    // =============================
    private LocalDate followUpDate;
    private LocalDate expectedLoginDate;

    @Column(length = 1000)
    private String comments;

    // =============================
    // OTHER
    // =============================
    private String leadSource;
    private String status;
    private String interestLevel;

    // =============================
    // RELATIONS
    // =============================
    private String tempLoanId;
    private String loanApplicationId;

    private Boolean isConverted = false;

    // =============================
    // OWNER
    // =============================
    private String ownerId;

    private String ownerName;
}