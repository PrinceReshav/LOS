package com.los.loanoriginatingsystem.kyc.voterid.dto;

import lombok.Data;

import java.util.List;

@Data
public class VoterIdOCRResponseDTO {

    private DataWrapper data;
    private Integer statusCode;
    private Boolean success;
    private String message;
    private String messageCode;

    @Data
    public static class DataWrapper {

        private String clientId;
        private List<OCRField> ocrFields;
    }

    @Data
    public static class OCRField {

        private String documentType;
        private OCRValue fullName;
        private OCRValue age;
        private OCRValue careOf;
        private OCRValue dob;
        private OCRValue doc;
        private OCRValue gender;
        private OCRValue epicNumber;
    }

    @Data
    public static class OCRValue {

        private String value;
        private String confidence;
    }
}
