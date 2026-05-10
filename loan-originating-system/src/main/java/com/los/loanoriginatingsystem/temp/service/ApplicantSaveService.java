package com.los.loanoriginatingsystem.temp.service;

import com.los.loanoriginatingsystem.temp.dto.ApplicantSaveRequestDTO;
import com.los.loanoriginatingsystem.temp.entity.TempLoanApplication;
import com.los.loanoriginatingsystem.temp.entity.embedded.ApplicantDetails;
import com.los.loanoriginatingsystem.temp.repository.TempLoanApplicationRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApplicantSaveService {

    private final TempLoanApplicationRepository repository;

    @Transactional
    public void saveApplicant(String tempId, ApplicantSaveRequestDTO req) {

        TempLoanApplication temp = repository.findById(tempId)
                .orElseThrow(() -> new RuntimeException("Temp not found"));

        if (temp.getApplicant() == null) {
            temp.setApplicant(new ApplicantDetails());
        }

        var applicant = temp.getApplicant();

        // =============================
        // 🔥 BASIC DETAILS
        // =============================
        applicant.setFirstName(req.getFirstName());
        applicant.setMiddleName(req.getMiddleName());
        applicant.setLastName(req.getLastName());

        applicant.setGender(req.getGender());
        applicant.setDob(req.getDob());

        // =============================
        // 🔥 CONTACT
        // =============================
        applicant.setEmail(req.getEmail());
        applicant.setAlternateMobileNumber(req.getAlternateMobile());

        // =============================
        // 🔥 PERSONAL DETAILS
        // =============================
        applicant.setNumberOfDependents(req.getNumberOfDependents());
        applicant.setEducationQualification(req.getEducationQualification());
        applicant.setMaritalStatus(req.getMaritalStatus());
        applicant.setReligion(req.getReligion());
        applicant.setCaste(req.getCaste());
        applicant.setCustomerCategory(req.getCustomerCategory());

        // =============================
        // 🔥 ADDRESS (CURRENT)
        // =============================
        applicant.setHouse(req.getHouse());
        applicant.setStreet(req.getStreet());
        applicant.setDistrict(req.getDistrict());
        applicant.setState(req.getState());
        applicant.setPincode(req.getPincode());

        // =============================
        // 🔥 FINAL FLAG
        // =============================
        temp.setIsApplicantCompleted(true);

        repository.save(temp);
    }
}