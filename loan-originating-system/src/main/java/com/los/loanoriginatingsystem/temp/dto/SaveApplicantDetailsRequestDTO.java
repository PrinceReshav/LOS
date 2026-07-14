package com.los.loanoriginatingsystem.temp.dto;

import lombok.Data;

@Data
public class SaveApplicantDetailsRequestDTO {

    // =====================================================
    // BASIC DETAILS
    // =====================================================

    private String title;

    private String firstName;
    private String middleName;
    private String lastName;

    private String gender;
    private String dob;
    private Integer age;

    private String mobileNumber;
    private String alternateMobileNumber;

    private String email;

    // =====================================================
    // PERSONAL DETAILS
    // =====================================================

    private String maritalStatus;

    private String religion;

    private String caste;

    private String educationQualification;

    private String customerCategory;

    private String differentlyAbledStatus;

    private Integer numberOfDependents;

    // =====================================================
    // FAMILY DETAILS
    // =====================================================

    private String motherFirstName;
    private String motherLastName;

    private String fatherFirstName;
    private String fatherLastName;

    private String spouseFirstName;
    private String spouseLastName;

    // =====================================================
    // PERMANENT ADDRESS
    // =====================================================

    private String permanentCareOf;

    private String permanentAddressLine1;
    private String permanentAddressLine2;

    private String permanentLandmark;

    private String permanentPincode;
    private String permanentCity;
    private String permanentState;

    private String permanentPropertyStatus;

    // =====================================================
    // RESIDENCE ADDRESS
    // =====================================================

    private String residenceAddressLine1;
    private String residenceAddressLine2;
    private String residenceAddressLine3;

    private String residenceLandmark;

    private String residencePincode;
    private String residenceCity;
    private String residenceState;

    private String residencePropertyStatus;

    private String areaCategory;

    // =====================================================
    // MAILING ADDRESS
    // =====================================================

    private Boolean mailingSameAsResidence;

    private Boolean mailingSameAsPermanent;

    private String mailingAddressLine1;
    private String mailingAddressLine2;
    private String mailingAddressLine3;

    private String mailingLandmark;

    private String mailingPincode;
    private String mailingCity;
    private String mailingState;

    private String mailingPropertyStatus;

    // =====================================================
    // BUSINESS DETAILS
    // =====================================================

    private Boolean businessSameAsResidence;

    private Boolean businessSameAsPermanent;

    private String businessName;

    private String businessType;

    private String businessAddressLine1;
    private String businessAddressLine2;
    private String businessAddressLine3;

    private String businessReference1Address;
    private String businessReference1Contact;

    private String businessReference2Address;
    private String businessReference2Contact;

    private String businessLandmark;

    private String businessPincode;
    private String businessCity;
    private String businessState;

    private String officePropertyStatus;

    private String businessAreaCategory;
}