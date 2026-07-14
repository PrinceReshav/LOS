package com.los.loanoriginatingsystem.temp.entity.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.Data;

@Embeddable
@Data
public class DlDetails {

    @Lob
    @Column(name = "dl_image_data")
    private String imageData;

    @Column(name = "dl_file_name")
    private String fileName;

    @Column(name = "dl_ocr_license_number")
    private String ocrLicenseNumber;

    @Column(name = "dl_ocr_dob")
    private String ocrDob;

    @Column(name = "dl_verified_license_number")
    private String verifiedLicenseNumber;

    @Column(name = "dl_verified_dob")
    private String verifiedDob;

    @Column(name = "dl_verified")
    private Boolean verified;

    @Column(name = "dl_clientId")
    private String clientId;
}