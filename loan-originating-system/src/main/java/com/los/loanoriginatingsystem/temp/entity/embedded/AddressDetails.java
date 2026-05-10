package com.los.loanoriginatingsystem.temp.entity.embedded;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class AddressDetails {

    // Aadhaar Address
    private String aadhaarHouse;
    private String aadhaarStreet;
    private String aadhaarDistrict;
    private String aadhaarState;
    private String aadhaarPincode;

    // Current Address
    private String currentHouse;
    private String currentStreet;
    private String currentDistrict;
    private String currentState;
    private String currentPincode;
    private String currentLandmark;

    private Boolean sameAsAadhaar;
}
