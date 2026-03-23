package com.los.loanoriginatingsystem.integration.logging.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="api_log")
public class ApiLog {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length=131072)
    private String requestBody;

    @Column(length=131072)
    private String responseBody;

    private String integrationType;

    private String applicationId;
}