package com.los.loanoriginatingsystem.temp.service.impl;

import com.los.loanoriginatingsystem.kyc.audit.service.KycAuditService;
import com.los.loanoriginatingsystem.temp.entity.TempLoanApplication;
import com.los.loanoriginatingsystem.temp.entity.embedded.KycStatus;
import com.los.loanoriginatingsystem.temp.repository.TempLoanApplicationRepository;
import com.los.loanoriginatingsystem.temp.service.kyc.KycTempService;
import com.los.loanoriginatingsystem.kyc.aadhaar.dto.*;
import com.los.loanoriginatingsystem.kyc.pan.dto.*;
import com.los.loanoriginatingsystem.kyc.drivinglicense.dto.*;
import com.los.loanoriginatingsystem.kyc.voterid.dto.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KycTempServiceImpl implements KycTempService {

    private final TempLoanApplicationRepository repo;
    private final KycAuditService auditService;


    // =============================
    // 🔁 COMMON METHODS
    // =============================
    private TempLoanApplication get(String id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Temp not found"));
    }

    private void save(TempLoanApplication temp) {
        repo.save(temp);
    }

    private void markKycCompleted(TempLoanApplication temp) {
        if (Boolean.TRUE.equals(temp.getAadhaar().getVerified())
                && Boolean.TRUE.equals(temp.getPan().getVerified())
                && Boolean.TRUE.equals(temp.getLiveness().getVerified())) {

            temp.setIsKycCompleted(true);
        }
    }

    private void updateKycCompletion(TempLoanApplication temp) {
        if (temp.getAadhaarStatus() == KycStatus.SUCCESS &&
                temp.getPanStatus() == KycStatus.SUCCESS &&
                temp.getLivenessStatus() == KycStatus.SUCCESS) {

            temp.setIsKycCompleted(true);
        }
    }
    private <T> T safe(T value, String message) {
        if (value == null) throw new RuntimeException(message);
        return value;
    }

    private <T> T first(java.util.List<T> list, String message) {
        if (list == null || list.isEmpty()) throw new RuntimeException(message);
        return list.get(0);
    }


    // =============================
    // 🔥 SAFE OCR EXTRACTOR (REUSABLE)
    // =============================
    private <T> T getFirstOrThrow(T value, String message) {
        if (value == null) {
            throw new RuntimeException(message);
        }
        return value;
    }

    private <T> T getFirstFromList(java.util.List<T> list, String message) {
        if (list == null || list.isEmpty()) {
            throw new RuntimeException(message);
        }
        return list.get(0);
    }

    // =============================
    // 🔐 AADHAAR
    // =============================
    @Override
    public void sendAadhaarOtp(String tempId, AadhaarGenerateOtpResponseDTO dto) {
        TempLoanApplication temp = get(tempId);
        temp.getAadhaar().setOtpSent(dto.getData().getOtpSent());
        save(temp);
    }

    @Override
    @Transactional
    public void processAadhaarOCR(String tempId, AadhaarOCRResponseDTO dto) {

        TempLoanApplication temp = get(tempId);

        try {

            var field = first(
                    safe(dto.getData(), "Aadhaar data missing").getOcrFields(),
                    "Aadhaar OCR fields missing"
            );

            temp.getAadhaar().setOcrName(field.getFullName().getValue());
            temp.getAadhaar().setOcrDob(field.getDob().getValue());
            temp.getAadhaar().setOcrGender(field.getGender().getValue());
            temp.getAadhaar().setOcrAadhaarNumber(field.getAadhaarNumber().getValue());

            temp.setAadhaarStatus(KycStatus.SUCCESS);

            save(temp);

            auditService.audit(tempId, "AADHAAR_OCR", "SUCCESS", null, dto.toString(), null);

        } catch (Exception e) {

            temp.setAadhaarStatus(KycStatus.FAILED);
            save(temp);

            auditService.audit(tempId, "AADHAAR_OCR", "FAILED", null, null, e.getMessage());

            throw new RuntimeException("Aadhaar OCR failed", e);
        }
    }

    @Override
    @Transactional
    public void verifyAadhaarOtp(String tempId, AadhaarSubmitOtpResponseDTO dto) {

        TempLoanApplication temp = get(tempId);

        if (temp.getAadhaarStatus() == KycStatus.SUCCESS) return;

        try {

            var data = safe(dto.getData(), "Aadhaar verify response missing");

            temp.getAadhaar().setOtpVerified(data.getMobileVerified());
            temp.getAadhaar().setVerifiedName(data.getFullName());
            temp.getAadhaar().setVerifiedAadhaarNumber(data.getAadhaarNumber());
            temp.getAadhaar().setVerifiedDob(data.getDob());
            temp.getAadhaar().setVerifiedGender(data.getGender());
            temp.getAadhaar().setVerified(true);

            if (data.getAddress() != null) {
                var addr = data.getAddress();
                temp.getAadhaar().setHouse(addr.getHouse());
                temp.getAadhaar().setStreet(addr.getStreet());
                temp.getAadhaar().setDistrict(addr.getDist());
                temp.getAadhaar().setState(addr.getState());
                temp.getAadhaar().setPincode(addr.getCountry());
            }

            temp.setAadhaarStatus(KycStatus.SUCCESS);

            updateKycCompletion(temp);
            save(temp);

            auditService.audit(tempId, "AADHAAR_VERIFY", "SUCCESS", null, dto.toString(), null);

        } catch (Exception e) {

            temp.setAadhaarStatus(KycStatus.FAILED);
            save(temp);

            auditService.audit(tempId, "AADHAAR_VERIFY", "FAILED", null, null, e.getMessage());

            throw new RuntimeException("Aadhaar verification failed", e);
        }
    }

    @Override
    public void verifyAadhaar(String tempId, AadhaarVerificationResponseDTO dto) {

        TempLoanApplication temp = get(tempId);

        temp.getAadhaar().setVerified(true);
        temp.getAadhaar().setVerifiedGender(dto.getData().getGender());
        temp.getAadhaar().setVerifiedAadhaarNumber(dto.getData().getAadhaarNumber());

        markKycCompleted(temp);
        save(temp);
    }


    // =============================
    // 🧾 PAN
    // =============================
    @Override
    public void processPanOCR(String tempId, PanOCRResponseDTO dto) {

        TempLoanApplication temp = get(tempId);

        var field = getFirstFromList(
                getFirstOrThrow(dto.getData(), "PAN data missing").getOcrFields(),
                "PAN OCR fields missing"
        );

        temp.getPan().setOcrPanNumber(field.getPanNumber().getValue());

        save(temp);
    }

    @Override
    @Transactional
    public void verifyPan(String tempId, PanVerificationResponseDTO dto) {

        TempLoanApplication temp = get(tempId);

        if (temp.getPanStatus() == KycStatus.SUCCESS) return;

        try {

            var data = safe(dto.getData(), "PAN verify response missing");

            temp.getPan().setVerifiedPanNumber(data.getPanNumber());
            temp.getPan().setVerifiedName(data.getFullName());
            temp.getPan().setVerified(true);

            temp.setPanStatus(KycStatus.SUCCESS);

            updateKycCompletion(temp);
            save(temp);

            auditService.audit(tempId, "PAN", "SUCCESS", null, dto.toString(), null);

        } catch (Exception e) {

            temp.setPanStatus(KycStatus.FAILED);
            save(temp);

            auditService.audit(tempId, "PAN", "FAILED", null, null, e.getMessage());

            throw new RuntimeException("PAN verification failed", e);
        }
    }



    // =============================
    // 📸 LIVENESS
    // =============================
    @Override
    @Transactional
    public void saveLiveness(String tempId, String base64) {

        TempLoanApplication temp = get(tempId);

        if (temp.getLivenessStatus() == KycStatus.SUCCESS) return;

        try {

            temp.getLiveness().setPhoto(base64);
            temp.getLiveness().setVerified(true);

            temp.setLivenessStatus(KycStatus.SUCCESS);

            updateKycCompletion(temp);
            save(temp);

            auditService.audit(tempId, "LIVENESS", "SUCCESS", null, null, null);

        } catch (Exception e) {

            temp.setLivenessStatus(KycStatus.FAILED);
            save(temp);

            auditService.audit(tempId, "LIVENESS", "FAILED", null, null, e.getMessage());

            throw new RuntimeException("Liveness failed", e);
        }
    }


    // =============================
    // 🚗 DL
    // =============================
    @Override
    public void processDlOCR(String tempId, DLOCRResponseDTO dto) {

        TempLoanApplication temp = get(tempId);

        var data = getFirstOrThrow(dto.getData(), "DL OCR data missing");

        temp.getDl().setOcrLicenseNumber(data.getLicenseNumber().getValue());
        temp.getDl().setOcrDob(data.getDob().getValue());

        save(temp);
    }

    @Override
    @Transactional
    public void verifyDl(String tempId, DLVerificationResponseDTO dto) {

        TempLoanApplication temp = get(tempId);

        try {

            var data = safe(dto.getData(), "DL verify missing");

            temp.getDl().setVerifiedLicenseNumber(data.getLicenseNumber());
            temp.getDl().setVerifiedDob(data.getDob());
            temp.getDl().setVerified(true);

            save(temp);

            auditService.audit(tempId, "DL", "SUCCESS", null, dto.toString(), null);

        } catch (Exception e) {

            auditService.audit(tempId, "DL", "FAILED", null, null, e.getMessage());

            throw new RuntimeException("DL verification failed", e);
        }
    }

    // =============================
    // 🗳️ VOTER
    // =============================
    @Override
    public void processVoterOCR(String tempId, VoterIdOCRResponseDTO dto) {

        TempLoanApplication temp = get(tempId);

        var field = getFirstFromList(
                getFirstOrThrow(dto.getData(), "Voter data missing").getOcrFields(),
                "Voter OCR fields missing"
        );

        temp.getVoter().setOcrEpicNumber(field.getEpicNumber().getValue());
        temp.getVoter().setOcrName(field.getFullName().getValue());

        save(temp);
    }

    @Override
    @Transactional
    public void verifyVoter(String tempId, VoterIdVerificationResponseDTO dto) {

        TempLoanApplication temp = get(tempId);

        try {

            var data = safe(dto.getData(), "Voter verify missing");

            temp.getVoter().setVerifiedEpicNumber(data.getEpicNo());
            temp.getVoter().setVerifiedName(data.getName());
            temp.getVoter().setVerified(true);

            save(temp);

            auditService.audit(tempId, "VOTER", "SUCCESS", null, dto.toString(), null);

        } catch (Exception e) {

            auditService.audit(tempId, "VOTER", "FAILED", null, null, e.getMessage());

            throw new RuntimeException("Voter verification failed", e);
        }
    }

}