package com.los.loanoriginatingsystem.kyc.aml.service;

import com.los.loanoriginatingsystem.kyc.aml.dto.AMLResponseDTO;

import java.util.List;

public interface AMLService {

    AMLResponseDTO getAMLData(List<String> amlDataList);

}