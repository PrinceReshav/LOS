package com.los.loanoriginatingsystem.kyc.pan.dto;

import lombok.Data;

import java.util.List;

@Data
public class PanOCRResponseDTO {

    private DataDTO data;
    private Integer statusCode;
    private String messageCode;
    private Object message;
    private Boolean success;

    @Data
    public static class DataDTO {

        private String clientId;
        private List<OcrFieldsDTO> ocrFields;
    }

    @Data
    public static class OcrFieldsDTO {

        private String documentType;
        private PanNumberDTO panNumber;
        private PanNumberDTO fullName;
        private PanNumberDTO fatherName;
        private PanNumberDTO dob;
    }

    @Data
    public static class PanNumberDTO {

        private String value;
        private Integer confidence;
    }
}