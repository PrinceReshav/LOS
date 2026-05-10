package com.los.loanoriginatingsystem.lead.dto;

import com.los.loanoriginatingsystem.lead.enums.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateLeadRequest {

    private String firstName;
    private String lastName;
    private String mobile;

    private Gender gender;
    private LeadSource source;
    private InterestLevel interestLevel;
    private BusinessType businessType;
    private State state;

    private LocalDate followUpDate;
    private LocalDate expectedLoginDate;
}
