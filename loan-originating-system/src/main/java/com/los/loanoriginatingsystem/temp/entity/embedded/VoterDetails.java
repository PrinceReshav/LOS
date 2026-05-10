package com.los.loanoriginatingsystem.temp.entity.embedded;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.Data;

@Embeddable
@Data
public class VoterDetails {

    @Lob
    private String imageData;

    private String fileName;

    private String ocrEpicNumber;
    private String ocrName;

    private String verifiedEpicNumber;
    private String verifiedName;

    private Boolean verified;
}