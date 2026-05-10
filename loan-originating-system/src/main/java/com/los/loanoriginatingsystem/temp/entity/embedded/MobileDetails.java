package com.los.loanoriginatingsystem.temp.entity.embedded;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class MobileDetails {

    private String mobileNumber;

    private String otp;

    private Boolean mobileVerified;

    private String clientId;
}