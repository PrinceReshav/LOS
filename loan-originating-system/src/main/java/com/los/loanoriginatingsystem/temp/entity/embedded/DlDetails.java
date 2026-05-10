package com.los.loanoriginatingsystem.temp.entity.embedded;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.Data;

@Embeddable
@Data
public class DlDetails {

    @Lob
    private String imageData;

    private String fileName;

    private String ocrLicenseNumber;
    private String ocrDob;

    private String verifiedLicenseNumber;
    private String verifiedDob;

    private Boolean verified;
}