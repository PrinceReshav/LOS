package com.los.loanoriginatingsystem.temp.entity.embedded;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.Data;

@Embeddable
@Data
public class AadhaarDetails {

    @Lob
    private String imageData;

    private String fileName;

    // OCR
    private String ocrName;
    private String ocrGender;
    private String ocrDob;
    private String ocrAadhaarNumber;

    // VERIFIED
    private String verifiedName;
    private String verifiedAadhaarNumber;
    private String verifiedDob;
    private String verifiedGender;

    // UNMASKED
    private String rawAadhaarNumber;           // UNMASKED (secure use)


    // ADDRESS
    private String house;
    private String street;
    private String district;
    private String state;
    private String pincode;
    private String landmark;

    // FLAGS
    private Boolean otpSent;
    private Boolean otpVerified;
    private Boolean verified;

    private String clientId;
}