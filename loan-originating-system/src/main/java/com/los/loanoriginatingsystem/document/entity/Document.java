package com.los.loanoriginatingsystem.document.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "document")
@Data
public class Document {

    @Id
    private String id;

    @Column(name = "temp_loan_id")
    private String tempLoanId;

    @Column(name = "loan_applicant_id")
    private String loanApplicantId;

    @Column(name = "loan_application_id")
    private String loanApplicationId;

    @Column(name = "document_type")
    private String documentType;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "content_type")
    private String contentType;

    @Lob
    @Column(name = "file_data")
    private String fileData;

    @Column(name = "preview_url")
    private String previewUrl;

    @Column(name = "status")
    private String status;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "is_processed")
    private Boolean isProcessed = false;

    @Lob
    @Column(name = "verification_response")
    private String verificationResponse;
}