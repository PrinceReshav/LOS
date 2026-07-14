package com.los.loanoriginatingsystem.temp.entity.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.Data;

@Embeddable
@Data
public class PanDetails {

    @Lob
    @Column(name = "pan_image_data")
    private String imageData;

    @Column(name = "pan_file_name")
    private String fileName;

    @Column(name = "pan_ocr_pan_number")
    private String ocrPanNumber;

    @Column(name = "pan_ocr_name")
    private String ocrName;

    @Column(name = "pan_verified_pan_number")
    private String verifiedPanNumber;

    @Column(name = "pan_verified_name")
    private String verifiedName;

    @Column(name = "pan_verified")
    private Boolean verified;

    @Column(name = "pan_client_id")
    private String clientId;
}