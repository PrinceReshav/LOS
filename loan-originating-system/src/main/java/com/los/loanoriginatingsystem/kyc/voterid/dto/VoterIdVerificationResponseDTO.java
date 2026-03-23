package com.los.loanoriginatingsystem.kyc.voterid.dto;

import lombok.Data;

import java.util.List;

@Data
public class VoterIdVerificationResponseDTO {

    private VoterData data;
    private Integer statusCode;
    private Boolean success;
    private String message;
    private String messageCode;

    @Data
    public static class VoterData {

        private String clientId;
        private String epicNo;
        private String gender;
        private String state;
        private String name;
        private String relationName;
        private String relationType;
        private String houseNo;
        private String dob;
        private String age;
        private String area;
        private List<String> additionalCheck;
        private Boolean multiple;
        private String lastUpdate;
        private String assemblyConstituency;
        private String assemblyConstituencyNumber;
        private String pollingStation;
        private String partNumber;
        private String partName;
        private String parliamentaryConstituency;
    }
}