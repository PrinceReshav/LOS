package com.los.loanoriginatingsystem.lead.sequence;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "lead_sequence")
@Data
public class LeadSequence {

    @Id
    private String name; // "LEAD"

    @Column(name = "sequence_value")
    private Long value;
}