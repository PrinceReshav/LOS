package com.los.loanoriginatingsystem.kyc.aadhaar.dto;

import lombok.Data;

@Data
public class AadhaarGenerateOtpResponseDTO {

    private String statusCode;
    private String message;
    private Boolean success;
    private ResultVerifyAadhaar data;

    @Data
    public static class ResultVerifyAadhaar {

        private String clientId;
        private Boolean otpSent;
        private Boolean ifNumber;
        private Boolean validAadhaar;
        private String status;
    }
}