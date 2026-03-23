package com.los.loanoriginatingsystem.kyc.pan.dto;

import lombok.Data;

@Data
public class PanVerificationResponseDTO {

    private String statusCode;
    private String message;
    private Boolean success;
    private PanDetails data;

    @Data
    public static class PanDetails {

        private String panNumber;
        private String fullName;
        private String clientId;
        private String category;
    }
}
