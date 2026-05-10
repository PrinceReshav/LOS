package com.los.loanoriginatingsystem.temp.entity.embedded;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.Data;

@Embeddable
@Data
public class PanDetails {

    @Lob
    private String imageData;

    private String fileName;

    // OCR
    private String ocrPanNumber;
    private String ocrName;

    // VERIFIED
    private String verifiedPanNumber;
    private String verifiedName;

    private Boolean verified;

    private String clientId;
}