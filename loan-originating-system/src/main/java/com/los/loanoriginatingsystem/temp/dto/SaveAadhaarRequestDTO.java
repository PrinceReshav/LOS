package com.los.loanoriginatingsystem.temp.dto;

import lombok.Data;

@Data
public class SaveAadhaarRequestDTO {

    private String aadhaarNumber;
    private String name;
    private String dob;
    private String gender;

    private String house;
    private String street;
    private String district;
    private String state;
    private String pincode;

    private String otp;
}
