package com.los.loanoriginatingsystem.temp.validation;

import com.los.loanoriginatingsystem.temp.entity.TempLoanApplication;
import org.springframework.stereotype.Component;

@Component
public class KycValidator {

    public void validate(TempLoanApplication temp) {

        // =============================
        // 📱 MOBILE
        // =============================
        if (!Boolean.TRUE.equals(temp.getMobile().getMobileVerified())) {
            throw new ValidationException("Mobile not verified");
        }

        // =============================
        // 🧾 AADHAAR
        // =============================
        if (!Boolean.TRUE.equals(temp.getAadhaar().getVerified())) {
            throw new ValidationException("Aadhaar not verified");
        }

        if (temp.getAadhaar().getVerifiedAadhaarNumber() == null) {
            throw new ValidationException("Aadhaar number missing");
        }

        // =============================
        // 🧾 PAN
        // =============================
        if (!Boolean.TRUE.equals(temp.getPan().getVerified())) {
            throw new ValidationException("PAN not verified");
        }

        if (temp.getPan().getVerifiedPanNumber() == null) {
            throw new ValidationException("PAN number missing");
        }

        // =============================
        // 📸 LIVENESS
        // =============================
        if (!Boolean.TRUE.equals(temp.getLiveness().getVerified())) {
            throw new ValidationException("Liveness not completed");
        }
    }
}