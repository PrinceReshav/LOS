package com.los.loanoriginatingsystem.temp.dto;

import lombok.Data;

@Data
public class CreateOrResumeTempRequestDTO {

    private String leadId;

    private Boolean isNew;
}