package com.los.loanoriginatingsystem.temp.service;

import com.los.loanoriginatingsystem.temp.dto.ApplicantAutoFillDTO;
import com.los.loanoriginatingsystem.temp.entity.TempLoanApplication;
import com.los.loanoriginatingsystem.temp.repository.TempLoanApplicationRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplicantAutoFillService {

    private final TempLoanApplicationRepository repository;

    public ApplicantAutoFillDTO getAutoFillData(String tempId) {

        TempLoanApplication temp = repository.findById(tempId)
                .orElseThrow(() -> new RuntimeException("Temp not found"));

        ApplicantAutoFillDTO dto = new ApplicantAutoFillDTO();

        // =============================
        // 🔥 PRIORITY LOGIC
        // =============================

        // Name → Aadhaar > PAN > DL/Voter
        if (temp.getAadhaar() != null && temp.getAadhaar().getVerifiedName() != null) {
            dto.setFirstName(temp.getAadhaar().getVerifiedName());
        } else if (temp.getPan() != null && temp.getPan().getVerifiedName() != null) {
            dto.setFirstName(temp.getPan().getVerifiedName());
        } else if (temp.getApplicant() != null && temp.getApplicant().getFirstName() != null) {
            dto.setFirstName(temp.getApplicant().getFirstName());
        }

        // DOB → Aadhaar > DL
        if (temp.getAadhaar() != null && temp.getAadhaar().getVerifiedDob() != null) {
            dto.setDob(temp.getAadhaar().getVerifiedDob());
        } else if (temp.getApplicant() != null && temp.getApplicant().getDob() != null) {
            dto.setDob(temp.getApplicant().getDob());
        }

        // Gender → Aadhaar > DL > Voter
        if (temp.getAadhaar() != null && temp.getAadhaar().getVerifiedGender() != null) {
            dto.setGender(temp.getAadhaar().getVerifiedGender());
        } else if (temp.getApplicant() != null && temp.getApplicant().getGender() != null) {
            dto.setGender(temp.getApplicant().getGender());
        }

        // Aadhaar (masked)
        if (temp.getAadhaar() != null) {
            dto.setAadhaarNumber(temp.getAadhaar().getVerifiedAadhaarNumber());
        }

        // PAN
        if (temp.getPan() != null) {
            dto.setPanNumber(temp.getPan().getVerifiedPanNumber());
        }

        // Mobile
        if (temp.getMobile() != null) {
            dto.setMobileNumber(temp.getMobile().getMobileNumber());
        }

        // Address → Aadhaar only
        if (temp.getAadhaar() != null) {
            dto.setHouse(temp.getAadhaar().getHouse());
            dto.setStreet(temp.getAadhaar().getStreet());
            dto.setDistrict(temp.getAadhaar().getDistrict());
            dto.setState(temp.getAadhaar().getState());
            dto.setPincode(temp.getAadhaar().getPincode());
        }

        return dto;
    }
}