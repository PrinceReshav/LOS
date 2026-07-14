package com.los.loanoriginatingsystem.applicant.entity;

import com.los.loanoriginatingsystem.applicant.enums.PropertyStatus;
import com.los.loanoriginatingsystem.lead.enums.BusinessType;
import com.los.loanoriginatingsystem.temp.entity.embedded.AreaCategory;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "loan_applicant")
@Data
@EntityListeners(AuditingEntityListener.class)
public class LoanApplicant {

    @Id
    private String id;

    private String loanApplicationId;

    @Column(name = "applicant_number", unique = true, nullable = false)
    private String applicantNumber;


    private Boolean isPrimary;
    private String applicantType;

    private String title;

    private String firstName;
    private String middleName;
    private String lastName;

    private String gender;
    private String dob;
    private Integer age;

    private String email;
    private String mobileNumber;
    private String alternateMobileNumber;

    private String maritalStatus;
    private String religion;
    private String caste;
    private String educationQualification;

    private Integer numberOfDependents;
    private String customerCategory;

    private String relationshipToApplicant;


    // FAMILY DETAILS

    private String motherFirstName;
    private String motherLastName;

    private String fatherFirstName;
    private String fatherLastName;

    private String spouseFirstName;
    private String spouseLastName;

    private String differentlyAbledStatus;



    private String permanentCareOf;

    private String permanentAddressLine1;
    private String permanentAddressLine2;

    private String permanentLandmark;

    private String permanentPincode;
    private String permanentCity;
    private String permanentState;

    @Enumerated(EnumType.STRING)
    private PropertyStatus permanentPropertyStatus;



    private String residenceAddressLine1;
    private String residenceAddressLine2;
    private String residenceAddressLine3;

    private String residenceLandmark;

    private String residencePincode;
    private String residenceCity;
    private String residenceState;

    @Enumerated(EnumType.STRING)
    private PropertyStatus residencePropertyStatus;

    @Enumerated(EnumType.STRING)
    private AreaCategory areaCategory;


    private Boolean mailingSameAsResidence;

    private Boolean mailingSameAsPermanent;

    private String mailingAddressLine1;
    private String mailingAddressLine2;
    private String mailingAddressLine3;

    private String mailingLandmark;

    private String mailingPincode;
    private String mailingCity;
    private String mailingState;

    @Enumerated(EnumType.STRING)
    private PropertyStatus mailingPropertyStatus;


    private Boolean businessSameAsResidence;

    private Boolean businessSameAsPermanent;

    private String businessName;

    @Enumerated(EnumType.STRING)
    private BusinessType businessType;

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

    @Enumerated(EnumType.STRING)
    private PropertyStatus officePropertyStatus;

    @Enumerated(EnumType.STRING)
    private AreaCategory businessAreaCategory;




    // KYC

    private String aadhaarNumber;
    private String aadhaarName;
    private String aadhaarDob;
    private String aadhaarGender;

    private String panNumber;
    private String panName;

    private String voterIdNumber;
    private String voterName;

    private String drivingLicenseNumber;
    private String drivingLicenseName;

    private String verifiedName;
    private String verifiedMobileNumber;



    // CREDIT BLOCK
    private Integer creditScore;
    private String creditScoreStatus;

    private Integer highMarkScore;
    private String highMarkScoreStatus;

    // AML

    private Double amlScore;

    private String amlName;
    private String amlGender;
    private String amlDob;

    private String amlStatus;
    private String amlComments;


    // CLIENT IDs

    private String aadhaarClientId;
    private String panClientId;
    private String voterClientId;
    private String dlClientId;
    private String mobileClientId;

    // NOMINEE

    private Boolean nomineeRequired;

    private String nomineeName;
    private String nomineeGender;
    private String nomineeRelationship;
    private String nomineeDob;

    private String nomineeHouse;
    private String nomineeDistrict;
    private String nomineeState;
    private String nomineePincode;


    // EMPLOYMENT

    private String occupation;

    private String employerName;

    private String designation;

    private Integer experience;

    private Double incomeMonthly;

    private Double expenseMonthly;

    private Double totalIncome;

    // AUDIT

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;


}