package com.los.loanoriginatingsystem.kyc.drivinglicense.dto;

import lombok.Data;

@Data
public class DLOCRResponseDTO {

    private DataDTO data;
    private Integer statusCode;
    private Boolean success;
    private String message;
    private String messageCode;

    @Data
    public static class DataDTO {

        private String documentType;
        private LicenseNumberDTO licenseNumber;
        private DateOfBirthDTO dob;
        private String imageUrl;
    }

    @Data
    public static class LicenseNumberDTO {

        private String value;
        private String confidence;
    }

    @Data
    public static class DateOfBirthDTO {

        private String value;
        private String confidence;
    }
}