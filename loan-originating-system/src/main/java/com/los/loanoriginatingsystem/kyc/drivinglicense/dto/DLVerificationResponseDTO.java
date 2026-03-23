package com.los.loanoriginatingsystem.kyc.drivinglicense.dto;

import lombok.Data;

@Data
public class DLVerificationResponseDTO {

    private String statusCode;
    private String message;
    private Boolean success;
    private ResultDL data;

    @Data
    public static class ResultDL {

        private String temporaryAddress;
        private String fatherOrHusbandName;
        private String doe;
        private String temporaryZip;
        private String permanentAddress;
        private String doi;
        private String clientId;
        private String citizenship;
        private String dob;
        private String permanentZip;
        private String gender;
        private String licenseNumber;
        private String name;
        private String state;
        private String olaName;
        private String olaCode;
    }
}