package com.los.loanoriginatingsystem.kyc.aml.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AMLResponseDTO {

    private DataDTO data;
    private Integer statusCode;
    private Boolean success;
    private String message;
    private String messageCode;

    @Data
    public static class DataDTO {

        private String clientId;
        private String name;
        private List<ResultDTO> results;
    }

    @Data
    public static class ResultDTO {

        private BigDecimal score;

        private List<String> name;
        private List<String> firstName;
        private List<String> lastName;
        private List<String> nationality;
        private List<String> position;
        private List<String> dateOfBirth;
        private List<String> country;
        private List<String> gender;
        private List<String> email;
        private List<String> notes;
        private List<String> birthPlace;
        private List<String> education;
    }
}