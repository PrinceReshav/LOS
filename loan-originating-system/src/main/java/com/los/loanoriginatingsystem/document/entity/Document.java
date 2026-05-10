package com.los.loanoriginatingsystem.document.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(
        name = "document",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_document_bank_statement_doc_id",
                        columnNames = "bank_statement_doc_id"
                )
        }
)
@Data
public class Document {

    @Id
    private String id;

    @Column(name = "application_id")
    private String applicationId;

    @Lob
    private String kycResponse;

    private String tempLoanId;
    private String loanApplicantId;
    private String loanApplicationId;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "document_type")
    private String documentType;

    private String status;



    @Transient
    private String previousBankStatementStatus; // for event detection

    @Column(name = "bank_statement_doc_id", nullable = false)
    private String bankStatementDocId;

    @Column(name = "bank_statement_status")
    private String bankStatementStatus;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "preview_url")
    private String previewUrl;

    @Column(name = "is_bank_statement_primary")
    private Boolean bankStatementPrimary;

    @Column(name = "aa_processing")
    private Boolean aaProcessing = false;

// =============================
// KYC / GENERIC DOCUMENT SUPPORT
// =============================

    @Lob
    @Column(name = "file_data")
    private String fileData; // base64 or external reference

    @Column(name = "kyc_type")
    private String kycType; // AADHAAR, PAN, DL, VOTER, LIVENESS

    @Column(name = "is_verified")
    private Boolean isVerified;

    @Column(name = "is_processed")
    private Boolean isProcessed;

}