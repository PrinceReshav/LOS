package com.los.loanoriginatingsystem.documentgeneration.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * An admin-editable, Thymeleaf-syntax HTML template used to generate a PDF
 * document for a loan application (Sanction Letter, Welcome Letter, Loan
 * Agreement, Rejection Letter, etc). Equivalent to Salesforce's Visualforce
 * document-generation pages (GenerateDocumentEsignController + ~60 VF
 * pages), collapsed into data-driven templates instead of one Apex/VF page
 * per document type - a new document type is a new DB row, not a deploy.
 *
 * Placeholders use standard Thymeleaf syntax against the merge model built
 * by DocumentMergeModelBuilder, e.g.:
 *   <span th:text="${applicantName}">Applicant Name</span>
 *   <span th:text="${approvedAmount}">0</span>
 */
@Entity
@Table(name = "document_template")
@Getter
@Setter
public class DocumentTemplate {

    @Id
    private String id;

    /** Stable code used to request generation, e.g. "SANCTION_LETTER", "WELCOME_LETTER". */
    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    private String description;

    /**
     * The stage(s) this document is typically generated at, comma
     * separated (e.g. "Sanctioned"), for admin-UI grouping only - not
     * enforced server-side, since a user may need to regenerate/preview a
     * document out of sequence.
     */
    private String applicableStage;

    /** Thymeleaf HTML template source. */
    @Lob
    @Column(name = "html_content", nullable = false)
    private String htmlContent;

    @Column(nullable = false)
    private Integer version = 1;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (active == null) active = true;
        if (version == null) version = 1;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
        version = (version == null ? 1 : version) + 1;
    }
}
