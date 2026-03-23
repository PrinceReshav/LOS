package com.los.loanoriginatingsystem.kyc.aadhaar.dto;

import lombok.Data;

@Data
public class AadhaarVerificationResponseDTO {

    private String statusCode;
    private String message;
    private Boolean success;
    private ResultAadhaar data;

    @Data
    public static class ResultAadhaar {

        private String ageRange;
        private String state;
        private Boolean isMobile;
        private String gender;
        private String aadhaarNumber;
        private String lastDigits;
        private String clientId;
    }
}