package com.los.loanoriginatingsystem.temp.dto;

import lombok.Data;

@Data
public class ApplicantAutoFillDTO {

    private String firstName;
    private String middleName;
    private String lastName;

    private String gender;
    private String dob;

    private String aadhaarNumber;
    private String panNumber;

    private String mobileNumber;

    // Address
    private String house;
    private String street;
    private String district;
    private String state;
    private String pincode;
}