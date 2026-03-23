package com.los.loanoriginatingsystem.banking.ifsc.service;

import com.los.loanoriginatingsystem.banking.ifsc.dto.IfscDetailsDTO;
import com.los.loanoriginatingsystem.banking.ifsc.entity.BankIfscDetail;
import com.los.loanoriginatingsystem.banking.ifsc.repository.BankIfscDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IfscLookupService {

    private final BankIfscDetailRepository repository;

    public IfscDetailsDTO getIFSCDetails(String ifscCode) {

        IfscDetailsDTO dto = new IfscDetailsDTO();

        repository.findByName(ifscCode).ifPresent(detail -> {

            dto.setBankName(detail.getBankName());

            dto.setBranchNameAndAddress(
                    detail.getBranchName() + ", " + detail.getBranchAddress()
            );
        });

        return dto;
    }
}