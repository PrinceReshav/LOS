package com.los.loanoriginatingsystem.temp.entity.embedded;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class ApplicantDetails {

    // BASIC
    private String firstName;
    private String middleName;
    private String lastName;
    private String title;

    private String gender;
    private String dob;
    private Integer age;

    private String email;

    private String mobileNumber;
    private String alternateMobileNumber;

    // PERSONAL
    private String fatherName;
    private String maritalStatus;
    private String religion;
    private String caste;
    private String educationQualification;

    private Integer numberOfDependents;

    // ADDRESS (AADHAAR → DEFAULT)
    private String house;
    private String street;
    private String district;
    private String state;
    private String pincode;
    private String landmark;

    // CURRENT ADDRESS
    private String currentHouse;
    private String currentStreet;
    private String currentDistrict;
    private String currentState;
    private String currentPincode;
    private String currentLandmark;

    // BUSINESS / CATEGORY
    private String customerCategory;

    // CREDIT / AML
    private Integer creditScore;
    private String creditScoreStatus;

    private Double amlScore;
    private String amlName;
    private String amlGender;
    private String amlDob;

    // CLIENT IDS (VERY IMPORTANT FOR APIs)
    private String aadhaarClientId;
    private String panClientId;
    private String voterClientId;
    private String dlClientId;
    private String mobileClientId;
}