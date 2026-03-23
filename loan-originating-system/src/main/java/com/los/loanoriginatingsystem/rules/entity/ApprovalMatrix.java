package com.los.loanoriginatingsystem.rules.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "approval_matrix")
@Data
public class ApprovalMatrix {

    @Id
    private String id;

    /**
     * 🔥 LEVEL (1,2,3,4,5)
     */
    private Integer level;

    /**
     * 🔥 ROLE (CBM, CCM, DBM, DCM...)
     */
    private String requiredRole;

    /**
     * 🔥 ORDER WITHIN SAME LEVEL
     */
    private Integer sequence;

    /**
     * 🔥 TYPE OF DEVIATION (optional future use)
     */
    private String deviationType;
}