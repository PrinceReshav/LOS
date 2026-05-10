package com.los.loanoriginatingsystem.temp.validation;

import com.los.loanoriginatingsystem.applicant.enums.*;
import com.los.loanoriginatingsystem.temp.entity.TempLoanApplication;
import org.springframework.stereotype.Component;

@Component
public class ApplicantValidator {

    public void validate(TempLoanApplication temp) {

        var applicant = temp.getApplicant();

        if (applicant == null) {
            throw new ValidationException("Applicant details missing");
        }
        // =============================
        // 🔥 ENUM VALIDATIONS (IMPORTANT)
        // =============================

        validateEnum(applicant.getGender(), Gender.class, "Invalid Gender");
        validateEnum(applicant.getMaritalStatus(), MaritalStatus.class, "Invalid Marital Status");
        validateEnum(applicant.getReligion(), Religion.class, "Invalid Religion");
        validateEnum(applicant.getCaste(), Caste.class, "Invalid Caste");
        validateEnum(applicant.getEducationQualification(), EducationQualification.class, "Invalid Education");
        validateEnum(applicant.getTitle(), Title.class, "Invalid Title");
        if (applicant.getFirstName() == null) {
            throw new ValidationException("First Name required");
        }

        if (applicant.getGender() == null) {
            throw new ValidationException("Gender required");
        }

        if (applicant.getDob() == null) {
            throw new ValidationException("DOB required");
        }

        if (applicant.getMobileNumber() == null) {
            throw new ValidationException("Mobile required");
        }

        // 🔥 FUTURE: CREDIT / AML / CIBIL checks will plug here
    }
    // ==================================//
    // GENERIC ENUM VALIDATOR (REUSABLE) //
    // ==================================//
    private <T extends Enum<T>> void validateEnum(
            String value,
            Class<T> enumClass,
            String errorMessage
    ) {
        if (value == null) return;

        try {
            Enum.valueOf(enumClass, value.toUpperCase());
        } catch (Exception e) {
            throw new ValidationException(errorMessage + ": " + value);
        }
    }
}