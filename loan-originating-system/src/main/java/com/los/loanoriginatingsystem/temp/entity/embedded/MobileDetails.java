package com.los.loanoriginatingsystem.temp.entity.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class MobileDetails {

    @Column(name = "mobile_number")
    private String mobileNumber;

    @Column(name = "mobile_otp")
    private String otp;

    @Column(name = "mobile_verified")
    private Boolean mobileVerified;

    @Column(name = "mobile_client_id")
    private String clientId;
}