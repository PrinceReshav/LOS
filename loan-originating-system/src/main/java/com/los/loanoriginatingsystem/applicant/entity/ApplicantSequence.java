package com.los.loanoriginatingsystem.applicant.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "applicant_sequence")
@Data
public class ApplicantSequence {

    @Id
    private String code;

    private Long value;
}