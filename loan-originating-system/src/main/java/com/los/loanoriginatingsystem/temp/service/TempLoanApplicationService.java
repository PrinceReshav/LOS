package com.los.loanoriginatingsystem.temp.service;

import com.los.loanoriginatingsystem.document.entity.Document;
import com.los.loanoriginatingsystem.document.repository.DocumentRepository;
import com.los.loanoriginatingsystem.temp.entity.TempLoanApplication;
import com.los.loanoriginatingsystem.temp.entity.embedded.*;
import com.los.loanoriginatingsystem.temp.repository.TempLoanApplicationRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TempLoanApplicationService {

    private final TempLoanApplicationRepository repository;
    private final DocumentRepository documentRepo;
    // =====================================================
    // 🚀 STEP 0 →  CREATE / RESUME / NEW
    // =====================================================
    @Transactional
    public TempLoanApplication createOrResume(String leadId, boolean isNew) {

        TempLoanApplication existing =
                repository.findByLeadId(leadId).orElse(null);

        // =============================
        // 🆕 FIRST TIME
        // =============================
        if (existing == null) {
            return createNew(leadId);
        }

        // =============================
        // 🔁 RESUME FLOW
        // =============================
        if (!isNew && !Boolean.TRUE.equals(existing.getIsSubmitted())) {
            return existing;
        }

        // =============================
        // ❌ BLOCK RESUME AFTER SUBMIT
        // =============================
        if (!isNew && Boolean.TRUE.equals(existing.getIsSubmitted())) {
            throw new RuntimeException("Cannot resume submitted application");
        }

        // =============================
        // 🆕 NEW FLOW (ALWAYS FRESH)
        // =============================
        if (isNew) {

            // 🔥 delete old temp
            repository.delete(existing);

            // 🔥 delete related docs
            List<Document> docs = documentRepo.findByTempLoanId(existing.getId());
            documentRepo.deleteAll(docs);

            return createNew(leadId);
        }

        throw new RuntimeException("Invalid flow state");
    }



    private TempLoanApplication createNew(String leadId) {

        TempLoanApplication temp = new TempLoanApplication();

        temp.setId(UUID.randomUUID().toString());
        temp.setLeadId(leadId);

        temp.setIsResume(false);
        temp.setIsKycCompleted(false);
        temp.setIsApplicantCompleted(false);
        temp.setIsSubmitted(false);

        return repository.save(temp);
    }



    // =====================================================
    // 🧾 STEP 1 → SAVE LOAN DETAILS
    // =====================================================
    public void saveLoanDetails(String tempId,
                                String purpose,
                                String loanType,
                                String productId,
                                String scheme,
                                BigDecimal amount,
                                Integer tenure) {

        TempLoanApplication temp = get(tempId);

        temp.getLoanDetails().setLoanPurpose(purpose);
        temp.getLoanDetails().setLoanType(loanType);
        temp.getLoanDetails().setLoanProductId(productId);
        temp.getLoanDetails().setLoanScheme(scheme);
        temp.getLoanDetails().setRequestedAmount(amount);
        temp.getLoanDetails().setTenureMonths(tenure);

        repository.save(temp);
    }

    // =====================================================
    // 📱 STEP 2 → SEND OTP
    // =====================================================
    public void sendOtp(String tempId, String mobile) {

        TempLoanApplication temp = get(tempId);

        // simulate OTP generation
        String otp = "123456";

        temp.getMobile().setMobileNumber(mobile);
        temp.getMobile().setOtp(otp);
        temp.getMobile().setMobileVerified(false);

        repository.save(temp);
    }

    // =====================================================
    // 📱 STEP 2 → VERIFY OTP
    // =====================================================
    public void verifyOtp(String tempId, String otp) {

        TempLoanApplication temp = get(tempId);

        if (!otp.equals(temp.getMobile().getOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        temp.getMobile().setMobileVerified(true);

        repository.save(temp);
    }

    // =====================================================
    // 🧾 STEP 3 → AADHAAR OCR SAVE
    // =====================================================
    public void saveAadhaarOcr(String tempId,
                               String name,
                               String dob,
                               String gender,
                               String aadhaarNumber) {

        TempLoanApplication temp = get(tempId);

        temp.getAadhaar().setOcrName(name);
        temp.getAadhaar().setOcrDob(dob);
        temp.getAadhaar().setOcrGender(gender);
        temp.getAadhaar().setOcrAadhaarNumber(aadhaarNumber);

        repository.save(temp);
    }

    // =====================================================
    // 🧾 STEP 3 → AADHAAR VERIFIED
    // =====================================================
    public void verifyAadhaar(String tempId,
                              String name,
                              String dob,
                              String gender,
                              String aadhaarNumber,
                              String house,
                              String street,
                              String district,
                              String state,
                              String pincode) {

        TempLoanApplication temp = get(tempId);

        temp.getAadhaar().setVerifiedName(name);
        temp.getAadhaar().setVerifiedDob(dob);
        temp.getAadhaar().setVerifiedGender(gender);
        temp.getAadhaar().setVerifiedAadhaarNumber(mask(aadhaarNumber));

        temp.getAadhaar().setHouse(house);
        temp.getAadhaar().setStreet(street);
        temp.getAadhaar().setDistrict(district);
        temp.getAadhaar().setState(state);
        temp.getAadhaar().setPincode(pincode);

        temp.getAadhaar().setVerified(true);

        repository.save(temp);
    }

    // =====================================================
    // 🧾 STEP 4 → PAN SAVE + VERIFY
    // =====================================================
    public void verifyPan(String tempId,
                          String pan,
                          String name) {

        TempLoanApplication temp = get(tempId);

        temp.getPan().setVerifiedPanNumber(pan);
        temp.getPan().setVerifiedName(name);
        temp.getPan().setVerified(true);

        repository.save(temp);
    }

    // =====================================================
    // 📸 STEP 5 → LIVENESS
    // =====================================================
    public void saveLiveness(String tempId, String base64) {

        TempLoanApplication temp = get(tempId);

        temp.getLiveness().setPhoto(base64);
        temp.getLiveness().setVerified(true);

        repository.save(temp);
    }

    // =====================================================
    // 🧑 STEP 6 → APPLICANT DETAILS
    // =====================================================
    public void saveApplicantDetails(String tempId,
                                     String firstName,
                                     String lastName,
                                     String gender,
                                     String dob,
                                     String email,
                                     String mobile) {

        TempLoanApplication temp = get(tempId);

        temp.getApplicant().setFirstName(firstName);
        temp.getApplicant().setLastName(lastName);
        temp.getApplicant().setGender(gender);
        temp.getApplicant().setDob(dob);
        temp.getApplicant().setEmail(email);
        temp.getApplicant().setMobileNumber(mobile);

        temp.setIsApplicantCompleted(true);

        repository.save(temp);
    }

    // =====================================================
    // 🧠 COMMON
    // =====================================================
    private TempLoanApplication get(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Temp not found"));
    }

    private String mask(String aadhaar) {
        if (aadhaar == null || aadhaar.length() < 4) return aadhaar;
        return "XXXX-XXXX-" + aadhaar.substring(aadhaar.length() - 4);
    }


}