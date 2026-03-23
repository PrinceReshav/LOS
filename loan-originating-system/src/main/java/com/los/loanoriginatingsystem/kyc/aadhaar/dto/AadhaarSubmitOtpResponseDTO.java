package com.los.loanoriginatingsystem.kyc.aadhaar.dto;

import lombok.Data;

@Data
public class AadhaarSubmitOtpResponseDTO {

    private String statusCode;
    private String message;
    private Boolean success;
    private String messageCode;
    private ResultVerifyAadhaar data;

    @Data
    public static class ResultVerifyAadhaar {

        private String clientId;
        private String fullName;
        private String aadhaarNumber;
        private String dob;
        private String gender;
        private AddressWrapper address;

        private Boolean faceStatus;
        private Double faceScore;

        private String zip;
        private String profileImage;
        private Boolean hasImage;

        private String rawXml;
        private String zipData;
        private String careOf;
        private String shareCode;

        private Boolean mobileVerified;
        private String referenceId;
        private String aadhaarPdf;
    }

    @Data
    public static class AddressWrapper {

        private String country;
        private String dist;
        private String state;
        private String po;
        private String loc;
        private String vtc;
        private String subdist;
        private String street;
        private String house;
        private String landmark;
    }
}