package com.los.loanoriginatingsystem.temp.entity.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class ApplicantDetails {

    // =====================================================
    // BASIC DETAILS
    // =====================================================

    @Column(name = "applicant_title")
    private String title;

    @Column(name = "applicant_first_name")
    private String firstName;

    @Column(name = "applicant_middle_name")
    private String middleName;

    @Column(name = "applicant_last_name")
    private String lastName;

    @Column(name = "applicant_gender")
    private String gender;

    @Column(name = "applicant_dob")
    private String dob;

    @Column(name = "applicant_age")
    private Integer age;

    @Column(name = "applicant_mobile_number")
    private String mobileNumber;

    @Column(name = "applicant_alternate_mobile_number")
    private String alternateMobileNumber;

    @Column(name = "applicant_email")
    private String email;

    // =====================================================
    // PERSONAL DETAILS
    // =====================================================

    @Column(name = "applicant_marital_status")
    private String maritalStatus;

    @Column(name = "applicant_religion")
    private String religion;

    @Column(name = "applicant_caste")
    private String caste;

    @Column(name = "applicant_education_qualification")
    private String educationQualification;

    @Column(name = "applicant_customer_category")
    private String customerCategory;

    @Column(name = "applicant_differently_abled_status")
    private String differentlyAbledStatus;

    @Column(name = "applicant_number_of_dependents")
    private Integer numberOfDependents;

    // =====================================================
    // FAMILY DETAILS
    // =====================================================

    @Column(name = "mother_first_name")
    private String motherFirstName;

    @Column(name = "mother_last_name")
    private String motherLastName;

    @Column(name = "father_first_name")
    private String fatherFirstName;

    @Column(name = "father_last_name")
    private String fatherLastName;

    @Column(name = "spouse_first_name")
    private String spouseFirstName;

    @Column(name = "spouse_last_name")
    private String spouseLastName;

    // =====================================================
    // PERMANENT ADDRESS
    // (LOCKED FROM AADHAAR)
    // =====================================================

    @Column(name = "permanent_care_of")
    private String permanentCareOf;

    @Column(name = "permanent_address_line1", length = 1000)
    private String permanentAddressLine1;

    @Column(name = "permanent_address_line2", length = 1000)
    private String permanentAddressLine2;

    @Column(name = "permanent_landmark")
    private String permanentLandmark;

    @Column(name = "permanent_pincode")
    private String permanentPincode;

    @Column(name = "permanent_city")
    private String permanentCity;

    @Column(name = "permanent_state")
    private String permanentState;

    @Column(name = "permanent_property_status")
    private String permanentPropertyStatus;

    // =====================================================
    // RESIDENCE ADDRESS
    // =====================================================

    @Column(name = "residence_address_line1", length = 1000)
    private String residenceAddressLine1;

    @Column(name = "residence_address_line2", length = 1000)
    private String residenceAddressLine2;

    @Column(name = "residence_address_line3", length = 1000)
    private String residenceAddressLine3;

    @Column(name = "residence_landmark")
    private String residenceLandmark;

    @Column(name = "residence_pincode")
    private String residencePincode;

    @Column(name = "residence_city")
    private String residenceCity;

    @Column(name = "residence_state")
    private String residenceState;

    @Column(name = "residence_property_status")
    private String residencePropertyStatus;

    @Column(name = "residence_area_category")
    private String areaCategory;

    // =====================================================
    // MAILING ADDRESS
    // =====================================================

    @Column(name = "mailing_same_as_residence")
    private Boolean mailingSameAsResidence;

    @Column(name = "mailing_same_as_permanent")
    private Boolean mailingSameAsPermanent;

    @Column(name = "mailing_address_line1", length = 1000)
    private String mailingAddressLine1;

    @Column(name = "mailing_address_line2", length = 1000)
    private String mailingAddressLine2;

    @Column(name = "mailing_address_line3", length = 1000)
    private String mailingAddressLine3;

    @Column(name = "mailing_landmark")
    private String mailingLandmark;

    @Column(name = "mailing_pincode")
    private String mailingPincode;

    @Column(name = "mailing_city")
    private String mailingCity;

    @Column(name = "mailing_state")
    private String mailingState;

    @Column(name = "mailing_property_status")
    private String mailingPropertyStatus;

    // =====================================================
    // BUSINESS DETAILS
    // =====================================================

    @Column(name = "business_same_as_residence")
    private Boolean businessSameAsResidence;

    @Column(name = "business_same_as_permanent")
    private Boolean businessSameAsPermanent;

    @Column(name = "business_name")
    private String businessName;

    @Column(name = "business_type")
    private String businessType;

    @Column(name = "business_address_line1", length = 1000)
    private String businessAddressLine1;

    @Column(name = "business_address_line2", length = 1000)
    private String businessAddressLine2;

    @Column(name = "business_address_line3", length = 1000)
    private String businessAddressLine3;

    @Column(name = "business_reference1_address", length = 1000)
    private String businessReference1Address;

    @Column(name = "business_reference1_contact")
    private String businessReference1Contact;

    @Column(name = "business_reference2_address", length = 1000)
    private String businessReference2Address;

    @Column(name = "business_reference2_contact")
    private String businessReference2Contact;

    @Column(name = "business_landmark")
    private String businessLandmark;

    @Column(name = "business_pincode")
    private String businessPincode;

    @Column(name = "business_city")
    private String businessCity;

    @Column(name = "business_state")
    private String businessState;

    @Column(name = "office_property_status")
    private String officePropertyStatus;

    @Column(name = "business_area_category")
    private String businessAreaCategory;

    // =====================================================
    // AML / CREDIT
    // =====================================================

    @Column(name = "credit_score")
    private Integer creditScore;

    @Column(name = "credit_score_status")
    private String creditScoreStatus;

    @Column(name = "aml_score")
    private Double amlScore;

    @Column(name = "aml_name")
    private String amlName;

    @Column(name = "aml_gender")
    private String amlGender;

    @Column(name = "aml_dob")
    private String amlDob;

}