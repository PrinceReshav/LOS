package com.los.loanoriginatingsystem.temp.entity.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.Data;

@Embeddable
@Data
public class VoterDetails {

    @Lob
    @Column(name = "voter_image_data")
    private String imageData;

    @Column(name = "voter_file_name")
    private String fileName;

    @Column(name = "voter_ocr_epic_number")
    private String ocrEpicNumber;

    @Column(name = "voter_ocr_name")
    private String ocrName;

    @Column(name = "voter_verified_epic_number")
    private String verifiedEpicNumber;

    @Column(name = "voter_verified_name")
    private String verifiedName;

    @Column(name = "voter_verified")
    private Boolean verified;

    @Column(name = "voter_clientId")
    private String clientId;
}