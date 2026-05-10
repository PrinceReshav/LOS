package com.los.loanoriginatingsystem.temp.dto;

import lombok.Data;

@Data
public class ApplicantSaveRequestDTO {

    private String firstName;
    private String middleName;
    private String lastName;

    private String gender;
    private String dob;

    private String email;
    private String alternateMobile;

    private Integer numberOfDependents;
    private String educationQualification;
    private String maritalStatus;
    private String religion;
    private String caste;
    private String customerCategory;

    // Address (Current)
    private String house;
    private String street;
    private String district;
    private String state;
    private String pincode;
}