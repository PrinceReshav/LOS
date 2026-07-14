package com.los.loanoriginatingsystem.temp.entity.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.Data;

@Embeddable
@Data
public class AadhaarDetails {

    @Lob
    @Column(name = "aadhaar_image_data")
    private String imageData;

    @Column(name = "aadhaar_file_name")
    private String fileName;

    @Column(name = "aadhaar_ocr_name")
    private String ocrName;

    @Column(name = "aadhaar_ocr_gender")
    private String ocrGender;

    @Column(name = "aadhaar_ocr_dob")
    private String ocrDob;

    @Column(name = "aadhaar_ocr_number")
    private String ocrAadhaarNumber;

    @Column(name = "aadhaar_verified_name")
    private String verifiedName;

    @Column(name = "aadhaar_verified_number")
    private String verifiedAadhaarNumber;

    @Column(name = "aadhaar_verified_dob")
    private String verifiedDob;

    @Column(name = "aadhaar_verified_gender")
    private String verifiedGender;

    @Column(name = "aadhaar_raw_number")
    private String rawAadhaarNumber;

    @Column(name = "aadhaar_house")
    private String house;

    @Column(name = "aadhaar_street")
    private String street;

    @Column(name = "aadhaar_district")
    private String district;

    @Column(name = "aadhaar_state")
    private String state;

    @Column(name = "aadhaar_pincode")
    private String pincode;

    @Column(name = "aadhaar_landmark")
    private String landmark;

    @Column(name = "aadhaar_otp_sent")
    private Boolean otpSent;

    @Column(name = "aadhaar_otp_verified")
    private Boolean otpVerified;

    @Column(name = "aadhaar_verified")
    private Boolean verified;

    @Column(name = "aadhaar_client_id")
    private String clientId;
}