package com.los.loanoriginatingsystem.kyc.aadhaar.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AadhaarOCRResponseDTO {

    private DataDTO data;
    private Integer statusCode;
    private Boolean success;
    private String message;
    private String messageCode;

    @Data
    public static class DataDTO {
        private String clientId;
        private List<OcrFieldsDTO> ocrFields;
    }

    @Data
    public static class OcrFieldsDTO {

        private String documentType;
        private FullNameDTO fullName;
        private FullNameDTO gender;
        private MotherNameDTO motherName;
        private FullNameDTO dob;
        private AadhaarNumberDTO aadhaarNumber;
        private String imageUrl;
        private String uniquenessId;
    }

    @Data
    public static class FullNameDTO {

        private String value;
        private BigDecimal confidence;
    }

    @Data
    public static class MotherNameDTO {

        private String value;
        private BigDecimal confidence;
    }

    @Data
    public static class AadhaarNumberDTO {

        private String value;
        private BigDecimal confidence;
        private Boolean isMasked;
    }
}