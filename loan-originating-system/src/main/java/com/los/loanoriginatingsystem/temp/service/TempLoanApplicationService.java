package com.los.loanoriginatingsystem.temp.service;

import com.los.loanoriginatingsystem.applicant.entity.LoanApplicant;
import com.los.loanoriginatingsystem.applicant.enums.PropertyStatus;
import com.los.loanoriginatingsystem.document.entity.Document;
import com.los.loanoriginatingsystem.document.repository.DocumentRepository;
import com.los.loanoriginatingsystem.document.service.DocumentMigrationService;
import com.los.loanoriginatingsystem.lead.entity.Lead;
import com.los.loanoriginatingsystem.lead.enums.BusinessType;
import com.los.loanoriginatingsystem.lead.enums.LeadStatus;
import com.los.loanoriginatingsystem.lead.repository.LeadRepository;
import com.los.loanoriginatingsystem.loan.entity.LoanApplication;
import com.los.loanoriginatingsystem.loanProduct.repository.LoanProductRepository;
import com.los.loanoriginatingsystem.temp.dto.ResumeTempApplicationResponseDTO;
import com.los.loanoriginatingsystem.temp.dto.SaveAadhaarRequestDTO;
import com.los.loanoriginatingsystem.temp.dto.SaveApplicantDetailsRequestDTO;
import com.los.loanoriginatingsystem.temp.dto.SavePanRequestDTO;
import com.los.loanoriginatingsystem.temp.entity.TempLoanApplication;
import com.los.loanoriginatingsystem.temp.entity.embedded.*;
import com.los.loanoriginatingsystem.temp.enums.ApplicationStage;
import com.los.loanoriginatingsystem.temp.repository.TempLoanApplicationRepository;
import com.los.loanoriginatingsystem.loan.repository.LoanApplicationRepository;
import lombok.RequiredArgsConstructor;
import com.los.loanoriginatingsystem.applicant.repository.LoanApplicantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class TempLoanApplicationService {

    private final TempLoanApplicationRepository repository;
    private final DocumentRepository documentRepo;
    private final DocumentMigrationService documentMigrationService;
    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanProductRepository loanProductRepository;
    private final LoanApplicantRepository loanApplicantRepository;
    private final LeadRepository leadRepository;

    private static final Logger log = LoggerFactory.getLogger(TempLoanApplicationService.class);

    // =====================================================
    // 🚀 STEP 0 →  CREATE / RESUME / NEW
    // =====================================================
    @Transactional
    public TempLoanApplication createOrResume(
            String leadId,
            boolean isNew
    ) {

        TempLoanApplication existing =
                repository.findByLeadId(leadId)
                        .orElse(null);

        // FIRST APPLICATION
        if (existing == null) {
            return createNew(leadId);
        }

        // RESUME EXISTING DRAFT
        if (!isNew) {

            if (Boolean.TRUE.equals(
                    existing.getIsSubmitted()
            )) {

                throw new RuntimeException(
                        "Cannot resume submitted application"
                );
            }

            return existing;
        }

        // START NEW APPLICATION

        List<Document> documents =
                documentRepo.findByTempLoanId(
                        existing.getId()
                );

        if (!documents.isEmpty()) {
            documentRepo.deleteAll(documents);
        }

        repository.delete(existing);

        repository.flush();

        return createNew(leadId);
    }



    private TempLoanApplication createNew(String leadId) {

        TempLoanApplication temp =
                new TempLoanApplication();

        temp.setId(UUID.randomUUID().toString());
        temp.setLeadId(leadId);

        temp.setIsResume(false);
        temp.setIsKycCompleted(false);
        temp.setIsApplicantCompleted(false);
        temp.setIsSubmitted(false);

        temp.setCurrentStage(
                ApplicationStage.LOAN_DETAILS
        );

        TempLoanApplication saved =
                repository.save(temp);

        Lead lead =
                leadRepository.findById(leadId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Lead not found"
                                )
                        );

        lead.setTempLoanId(
                saved.getId()
        );

        lead.setStatus(
                LeadStatus.IN_PROGRESS.name()
        );

        leadRepository.save(lead);

        return saved;
    }

    private void moveToStage(
            TempLoanApplication temp,
            ApplicationStage stage
    ) {
        temp.setCurrentStage(stage);
    }



    // BACK BUTTON IMPLEMENTATION
    @Transactional
    public void moveToPreviousStage(
            String tempId
    ) {

        TempLoanApplication temp =
                getForUpdate(tempId);

        switch (temp.getCurrentStage()) {

            case MOBILE_VERIFICATION ->
                    temp.setCurrentStage(
                            ApplicationStage.LOAN_DETAILS
                    );

            case AADHAAR_VERIFICATION ->
                    temp.setCurrentStage(
                            ApplicationStage.MOBILE_VERIFICATION
                    );

            case PAN_VERIFICATION ->
                    temp.setCurrentStage(
                            ApplicationStage.AADHAAR_VERIFICATION
                    );

            case DRIVING_LICENSE ->
                    temp.setCurrentStage(
                            ApplicationStage.PAN_VERIFICATION
                    );

            case VOTER_ID ->
                    temp.setCurrentStage(
                            ApplicationStage.DRIVING_LICENSE
                    );

            case APPLICANT_DETAILS ->
                    temp.setCurrentStage(
                            ApplicationStage.VOTER_ID
                    );

            case REVIEW ->
                    temp.setCurrentStage(
                            ApplicationStage.APPLICANT_DETAILS
                    );

            default ->
                    throw new RuntimeException(
                            "Cannot move back from current stage"
                    );
        }

        repository.save(temp);
    }

    public TempLoanApplication getById(
            String tempId
    ) {

        return repository.findById(tempId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Temp Application not found"
                        )
                );
    }


    // =====================================================
    // 🧾 STEP 1 → SAVE LOAN DETAILS
    // =====================================================

    @Transactional
    public void saveLoanDetails(String tempId,
                                String purpose,
                                String loanType,
                                String productId,
                                String scheme,
                                BigDecimal amount,
                                Integer tenure) {

        TempLoanApplication temp = getForUpdate(tempId);

        if (temp.getLoanDetails() == null) {

            temp.setLoanDetails(
                    new LoanDetails()
            );

        }

        temp.getLoanDetails().setLoanPurpose(purpose);
        temp.getLoanDetails().setLoanType(loanType);
        temp.getLoanDetails().setLoanProductId(productId);
        temp.getLoanDetails().setLoanProductCode(productId);

        temp.getLoanDetails().setLoanScheme(scheme);
        temp.getLoanDetails().setRequestedAmount(amount);
        temp.getLoanDetails().setTenureMonths(tenure);

        temp.setIsResume(true);

        moveToStage(
                temp,
                ApplicationStage.MOBILE_VERIFICATION
        );

        repository.save(temp);
    }

    // =====================================================
    // 📱 STEP 2 → SEND OTP
    // =====================================================
    @Transactional
    public void sendOtp(
            String tempId,
            String mobile
    ) {

        TempLoanApplication temp = getForUpdate(tempId);

        if (temp.getCurrentStage()
                != ApplicationStage.MOBILE_VERIFICATION) {

            throw new RuntimeException(
                    "Application is not in Mobile Verification stage"
            );
        }


        if (mobile == null ||
                !mobile.matches("\\d{10}")) {

            throw new RuntimeException(
                    "Invalid Mobile Number"
            );
        }

        if (temp.getMobile() == null) {

            temp.setMobile(
                    new MobileDetails()
            );
        }

        // FOR FUTURE
       /* if (otp == null || otp.isBlank()) {
        *    throw new RuntimeException("OTP generation failed");
        }*/

        temp.getMobile()
                .setMobileNumber(mobile);

        temp.getMobile()
                .setOtp("123456");

        temp.getMobile()
                .setMobileVerified(false);

        repository.save(temp);

    }

    // =====================================================
    // 📱 STEP 3 → VERIFY OTP
    // =====================================================
    @Transactional
    public void verifyOtp(
            String tempId,
            String otp
    ) {

        TempLoanApplication temp = getForUpdate(tempId);

        if (temp.getCurrentStage()
                != ApplicationStage.MOBILE_VERIFICATION) {

            throw new RuntimeException(
                    "Invalid application stage"
            );
        }

        if (temp.getMobile() == null) {
            throw new RuntimeException(
                    "OTP not generated"
            );
        }

        if (temp.getMobile().getOtp() == null) {
            throw new RuntimeException(
                    "OTP not generated"
            );
        }


        if (!otp.equals(
                temp.getMobile().getOtp()
        )) {
            throw new RuntimeException(
                    "Invalid OTP"
            );
        }

        temp.getMobile()
                .setMobileVerified(true);

        moveToStage(
                temp,
                ApplicationStage.AADHAAR_VERIFICATION
        );

        repository.save(temp);
    }


    @Transactional
    public void saveAadhaar(
            String tempId,
            SaveAadhaarRequestDTO request
    ) {

        TempLoanApplication temp = getForUpdate(tempId);

        if (temp.getCurrentStage()
                != ApplicationStage.AADHAAR_VERIFICATION) {

            throw new RuntimeException(
                    "Invalid application stage"
            );
        }

        AadhaarDetails aadhaar =
                temp.getAadhaar();

        if (aadhaar == null) {
            aadhaar = new AadhaarDetails();
            temp.setAadhaar(aadhaar);
        }

        aadhaar.setVerifiedAadhaarNumber(
                request.getAadhaarNumber()
        );

        aadhaar.setVerifiedName(
                request.getName()
        );

        aadhaar.setVerifiedDob(
                request.getDob()
        );

        aadhaar.setVerifiedGender(
                request.getGender()
        );

        aadhaar.setHouse(
                request.getHouse()
        );

        aadhaar.setStreet(
                request.getStreet()
        );

        aadhaar.setDistrict(
                request.getDistrict()
        );

        aadhaar.setState(
                request.getState()
        );

        aadhaar.setPincode(
                request.getPincode()
        );

        aadhaar.setVerified(true);

        temp.setAadhaarStatus(
                KycStatus.SUCCESS
        );

        moveToStage(
                temp,
                ApplicationStage.PAN_VERIFICATION
        );

        repository.save(temp);
    }

    @Transactional
    public void savePan(
            String tempId,
            SavePanRequestDTO request
    ) {

        TempLoanApplication temp = getForUpdate(tempId);

        if (temp.getCurrentStage()
                != ApplicationStage.PAN_VERIFICATION) {

            throw new RuntimeException(
                    "Application is not in PAN Verification stage"
            );
        }

        if (request.getPanNumber() == null
                || request.getPanNumber().isBlank()) {

            throw new RuntimeException(
                    "PAN Number is required"
            );
        }

        PanDetails pan = temp.getPan();

        if (pan == null) {
            pan = new PanDetails();
            temp.setPan(pan);
        }

        pan.setVerifiedPanNumber(
                request.getPanNumber()
        );

        pan.setVerifiedName(
                request.getName()
        );

        pan.setVerified(true);

        temp.setPanStatus(KycStatus.SUCCESS);

        temp.setIsKycCompleted(true);

        moveToStage(
                temp,
                ApplicationStage.APPLICANT_DETAILS
        );

        repository.save(temp);
    }





    // =====================================================
// 🧑 STEP 4 → APPLICANT DETAILS
// =====================================================

    @Transactional
    public void saveApplicant(
            String tempId,
            SaveApplicantDetailsRequestDTO request
    ) {

        TempLoanApplication temp =
                getForUpdate(tempId);

        if (
                temp.getCurrentStage() != ApplicationStage.APPLICANT_DETAILS
                        &&
                        temp.getCurrentStage() != ApplicationStage.REVIEW
        ) {

            throw new RuntimeException(
                    "Application cannot be edited in current stage"
            );
        }

        if (!Boolean.TRUE.equals(
                temp.getIsKycCompleted()
        )) {

            throw new RuntimeException(
                    "KYC not completed"
            );
        }

        ApplicantDetails applicant =
                temp.getApplicant();

        if (applicant == null) {

            applicant = new ApplicantDetails();

            temp.setApplicant(
                    applicant
            );
        }

        // =====================================================
        // BASIC DETAILS
        // =====================================================

        applicant.setTitle(
                request.getTitle()
        );

        applicant.setFirstName(
                request.getFirstName()
        );

        applicant.setMiddleName(
                request.getMiddleName()
        );

        applicant.setLastName(
                request.getLastName()
        );

        applicant.setGender(
                request.getGender()
        );

        applicant.setDob(
                request.getDob()
        );

        applicant.setAge(
                request.getAge()
        );

        applicant.setEmail(
                request.getEmail()
        );

        applicant.setMobileNumber(
                request.getMobileNumber()
        );

        applicant.setAlternateMobileNumber(
                request.getAlternateMobileNumber()
        );

        // =====================================================
        // PERSONAL DETAILS
        // =====================================================

        applicant.setMaritalStatus(
                request.getMaritalStatus()
        );

        applicant.setReligion(
                request.getReligion()
        );

        applicant.setCaste(
                request.getCaste()
        );

        applicant.setEducationQualification(
                request.getEducationQualification()
        );

        applicant.setCustomerCategory(
                request.getCustomerCategory()
        );

        applicant.setDifferentlyAbledStatus(
                request.getDifferentlyAbledStatus()
        );

        applicant.setNumberOfDependents(
                request.getNumberOfDependents()
        );

        // =====================================================
        // FAMILY DETAILS
        // =====================================================

        applicant.setMotherFirstName(
                request.getMotherFirstName()
        );

        applicant.setMotherLastName(
                request.getMotherLastName()
        );

        applicant.setFatherFirstName(
                request.getFatherFirstName()
        );

        applicant.setFatherLastName(
                request.getFatherLastName()
        );

        applicant.setSpouseFirstName(
                request.getSpouseFirstName()
        );

        applicant.setSpouseLastName(
                request.getSpouseLastName()
        );

        // =====================================================
        // PERMANENT ADDRESS
        // =====================================================

        applicant.setPermanentCareOf(
                request.getPermanentCareOf()
        );

        applicant.setPermanentAddressLine1(
                request.getPermanentAddressLine1()
        );

        applicant.setPermanentAddressLine2(
                request.getPermanentAddressLine2()
        );

        applicant.setPermanentLandmark(
                request.getPermanentLandmark()
        );

        applicant.setPermanentPincode(
                request.getPermanentPincode()
        );

        applicant.setPermanentCity(
                request.getPermanentCity()
        );

        applicant.setPermanentState(
                request.getPermanentState()
        );

        applicant.setPermanentPropertyStatus(
                request.getPermanentPropertyStatus()
        );

        // =====================================================
        // RESIDENCE ADDRESS
        // =====================================================

        applicant.setResidenceAddressLine1(
                request.getResidenceAddressLine1()
        );

        applicant.setResidenceAddressLine2(
                request.getResidenceAddressLine2()
        );

        applicant.setResidenceAddressLine3(
                request.getResidenceAddressLine3()
        );

        applicant.setResidenceLandmark(
                request.getResidenceLandmark()
        );

        applicant.setResidencePincode(
                request.getResidencePincode()
        );

        applicant.setResidenceCity(
                request.getResidenceCity()
        );

        applicant.setResidenceState(
                request.getResidenceState()
        );

        applicant.setResidencePropertyStatus(
                request.getResidencePropertyStatus()
        );

        applicant.setAreaCategory(
                request.getAreaCategory()
        );

        // =====================================================
        // MAILING ADDRESS
        // =====================================================

        applicant.setMailingSameAsResidence(
                request.getMailingSameAsResidence()
        );

        applicant.setMailingSameAsPermanent(
                request.getMailingSameAsPermanent()
        );

        applicant.setMailingAddressLine1(
                request.getMailingAddressLine1()
        );

        applicant.setMailingAddressLine2(
                request.getMailingAddressLine2()
        );

        applicant.setMailingAddressLine3(
                request.getMailingAddressLine3()
        );

        applicant.setMailingLandmark(
                request.getMailingLandmark()
        );

        applicant.setMailingPincode(
                request.getMailingPincode()
        );

        applicant.setMailingCity(
                request.getMailingCity()
        );

        applicant.setMailingState(
                request.getMailingState()
        );

        applicant.setMailingPropertyStatus(
                request.getMailingPropertyStatus()
        );
        // =====================================================
        // BUSINESS DETAILS
        // =====================================================

        applicant.setBusinessSameAsResidence(
                request.getBusinessSameAsResidence()
        );

        applicant.setBusinessSameAsPermanent(
                request.getBusinessSameAsPermanent()
        );

        applicant.setBusinessName(
                request.getBusinessName()
        );

        applicant.setBusinessType(
                request.getBusinessType()
        );

        applicant.setBusinessAddressLine1(
                request.getBusinessAddressLine1()
        );

        applicant.setBusinessAddressLine2(
                request.getBusinessAddressLine2()
        );

        applicant.setBusinessAddressLine3(
                request.getBusinessAddressLine3()
        );

        applicant.setBusinessReference1Address(
                request.getBusinessReference1Address()
        );

        applicant.setBusinessReference1Contact(
                request.getBusinessReference1Contact()
        );

        applicant.setBusinessReference2Address(
                request.getBusinessReference2Address()
        );

        applicant.setBusinessReference2Contact(
                request.getBusinessReference2Contact()
        );

        applicant.setBusinessLandmark(
                request.getBusinessLandmark()
        );

        applicant.setBusinessPincode(
                request.getBusinessPincode()
        );

        applicant.setBusinessCity(
                request.getBusinessCity()
        );

        applicant.setBusinessState(
                request.getBusinessState()
        );

        applicant.setOfficePropertyStatus(
                request.getOfficePropertyStatus()
        );

        applicant.setBusinessAreaCategory(
                request.getBusinessAreaCategory()
        );

        // =====================================================
        // FLOW FLAGS
        // =====================================================



        repository.save(
                temp
        );
    }


    @Transactional
    public void completeApplicant(String tempId) {

        TempLoanApplication temp =
                getForUpdate(tempId);

        temp.setIsApplicantCompleted(true);

        moveToStage(
                temp,
                ApplicationStage.REVIEW
        );

        repository.save(temp);
    }

    private LoanApplication createLoanApplication(
            TempLoanApplication temp
    ) {

        LoanApplication loanApplication =
                new LoanApplication();

        loanApplication.setId(
                UUID.randomUUID().toString()
        );

        String applicationNumber =
                generateApplicationNumber();

        loanApplication.setApplicationNumber(
                applicationNumber
        );

        loanApplication.setLoanAccountNumber(
                applicationNumber
        );

        loanApplication.setTempId(
                temp.getId()
        );

        loanApplication.setLeadId(
                temp.getLeadId()
        );

        loanApplication.setLoanPurpose(
                temp.getLoanDetails().getLoanPurpose()
        );

        loanApplication.setLoanType(
                temp.getLoanDetails().getLoanType()
        );

        loanApplication.setLoanProductId(
                temp.getLoanDetails().getLoanProductId()
        );

        loanApplication.setLoanProductCode(
                temp.getLoanDetails().getLoanProductCode()
        );

        loanApplication.setLoanScheme(
                temp.getLoanDetails().getLoanScheme()
        );

        loanApplication.setRequestedAmount(
                temp.getLoanDetails().getRequestedAmount()
        );

        loanApplication.setTenureMonths(
                temp.getLoanDetails().getTenureMonths()
        );

        if (temp.getApplicant() != null) {

            loanApplication.setApplicantName(
                    temp.getApplicant().getFirstName()
                            + " "
                            + temp.getApplicant().getLastName()
            );

            loanApplication.setEmail(
                    temp.getApplicant().getEmail()
            );
        }

        if (temp.getMobile() != null) {

            loanApplication.setMobileNumber(
                    temp.getMobile().getMobileNumber()
            );
        }

        loanApplication.setStage(
                "DATA_ENTRY"
        );

        loanApplication.setIsApplicantCompleted(
                temp.getIsApplicantCompleted()
        );

        loanApplication.setIsKycCompleted(
                temp.getIsKycCompleted()
        );

        return loanApplication;
    }

    private String generateApplicationNumber() {

        return "LOS-"
                + System.currentTimeMillis();
    }

    // =====================================================
    // 🧠 COMMON
    // =====================================================
    private TempLoanApplication getForUpdate(String id) {
        return repository.findByIdForUpdate(id)
                .orElseThrow(
                        () -> new RuntimeException("Temp not found")
                );
    }

    private String mask(String aadhaar) {
        if (aadhaar == null || aadhaar.length() < 4) return aadhaar;
        return "XXXX-XXXX-" + aadhaar.substring(aadhaar.length() - 4);
    }

    @Transactional
    public void submitApplication(
            String tempId
    ) {

        Optional<LoanApplication> existing =
                loanApplicationRepository
                        .findByTempId(tempId);

        if (existing.isPresent()) {

            log.info(
                    "Application already exists for tempId {}",
                    tempId
            );

            return;
        }

        log.info("SUBMIT START : {}", tempId);

        TempLoanApplication temp =
                getForUpdate(tempId);

        if (Boolean.TRUE.equals(
                temp.getIsSubmitted()
        )) {

            throw new RuntimeException(
                    "Application already submitted"
            );
        }

        if (!Boolean.TRUE.equals(
                temp.getIsKycCompleted()
        )) {

            throw new RuntimeException(
                    "KYC not completed"
            );
        }

        if (!Boolean.TRUE.equals(
                temp.getIsApplicantCompleted()
        )) {

            throw new RuntimeException(
                    "Applicant details not completed"
            );
        }

        if (temp.getMobile() == null ||
                !Boolean.TRUE.equals(
                        temp.getMobile()
                                .getMobileVerified()
                )) {

            throw new RuntimeException(
                    "Mobile not verified"
            );
        }

        try {

            LoanApplication loanApplication =
                    createLoanApplication(temp);

            loanApplicationRepository.save(
                    loanApplication
            );

            LoanApplicant applicant =
                    createLoanApplicant(
                            temp,
                            loanApplication
                    );

            loanApplicantRepository.save(
                    applicant
            );

            // Link the applicant back onto the loan application
            loanApplication.setPrimaryApplicantId(
                    applicant.getId()
            );

            loanApplicationRepository.save(
                    loanApplication
            );

            // Link any documents uploaded during the temp stage
            // to the newly created loan application / applicant,
            // and detach them from the (soon to be deleted) temp record
            documentMigrationService.migrateDocuments(
                    temp.getId(),
                    applicant.getId(),
                    loanApplication.getId()
            );

            // Mark the originating lead as converted
            markLeadConverted(
                    temp.getLeadId(),
                    loanApplication.getId()
            );

            log.info(
                    "APPLICATION SUBMITTED SUCCESSFULLY : {}",
                    loanApplication.getId()
            );

            // The temp application's job is done. Delete it entirely
            // rather than leaving a stale SUBMITTED row behind — the
            // LoanApplication (with its own applicationNumber) is now
            // the permanent record of this application.
            repository.delete(temp);

        } catch (Exception e) {

            log.error(
                    "APPLICATION SUBMISSION FAILED : {}",
                    tempId,
                    e
            );

            throw new RuntimeException(
                    "Submit failed : "
                            + e.getMessage(),
                    e
            );
        }
    }

    // Marks the originating Lead as CONVERTED once the
    // LoanApplication has been created. Uses LeadRepository
    // directly (rather than LeadService) to avoid a circular
    // bean dependency, since LeadService already depends on
    // this service.
    private void markLeadConverted(
            String leadId,
            String loanApplicationId
    ) {

        Lead lead =
                leadRepository.findById(leadId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Lead not found : " + leadId
                                )
                        );

        if (Boolean.TRUE.equals(
                lead.getIsConverted()
        )) {

            log.info(
                    "Lead already converted : {}",
                    leadId
            );

            return;
        }

        lead.setLoanApplicationId(
                loanApplicationId
        );

        lead.setTempLoanId(
                null
        );

        lead.setIsConverted(
                true
        );

        lead.setStatus(
                LeadStatus.CONVERTED.name()
        );

        leadRepository.save(lead);

        log.info(
                "Lead converted successfully. Lead={} Loan={}",
                leadId,
                loanApplicationId
        );
    }

    private LoanApplicant createLoanApplicant(
            TempLoanApplication temp,
            LoanApplication loanApplication
    ) {

        ApplicantDetails a =
                temp.getApplicant();

        LoanApplicant applicant =
                new LoanApplicant();

        if (temp.getDl() != null) {
            applicant.setDlClientId(
                    temp.getDl().getClientId()
            );
        }

        if (temp.getVoter() != null) {
            applicant.setVoterClientId(
                    temp.getVoter().getClientId()
            );
        }

        applicant.setId(
                UUID.randomUUID().toString()
        );

        applicant.setLoanApplicationId(
                loanApplication.getId()
        );

        applicant.setApplicantNumber(
                generateApplicantNumber()
        );

        applicant.setIsPrimary(true);

        applicant.setApplicantType(
                "PRIMARY"
        );

        // =====================================================
        // BASIC DETAILS
        // =====================================================

        applicant.setTitle(
                a.getTitle()
        );

        applicant.setFirstName(
                a.getFirstName()
        );

        applicant.setMiddleName(
                a.getMiddleName()
        );

        applicant.setLastName(
                a.getLastName()
        );

        applicant.setGender(
                a.getGender()
        );

        applicant.setDob(
                a.getDob()
        );

        applicant.setAge(
                a.getAge()
        );

        applicant.setEmail(
                a.getEmail()
        );

        applicant.setMobileNumber(
                a.getMobileNumber()
        );

        applicant.setAlternateMobileNumber(
                a.getAlternateMobileNumber()
        );

        // =====================================================
        // PERSONAL DETAILS
        // =====================================================

        applicant.setMaritalStatus(
                a.getMaritalStatus()
        );

        applicant.setReligion(
                a.getReligion()
        );

        applicant.setCaste(
                a.getCaste()
        );

        applicant.setEducationQualification(
                a.getEducationQualification()
        );

        applicant.setCustomerCategory(
                a.getCustomerCategory()
        );

        applicant.setDifferentlyAbledStatus(
                a.getDifferentlyAbledStatus()
        );

        applicant.setNumberOfDependents(
                a.getNumberOfDependents()
        );

        // =====================================================
        // FAMILY DETAILS
        // =====================================================

        applicant.setMotherFirstName(
                a.getMotherFirstName()
        );

        applicant.setMotherLastName(
                a.getMotherLastName()
        );

        applicant.setFatherFirstName(
                a.getFatherFirstName()
        );

        applicant.setFatherLastName(
                a.getFatherLastName()
        );

        applicant.setSpouseFirstName(
                a.getSpouseFirstName()
        );

        applicant.setSpouseLastName(
                a.getSpouseLastName()
        );

        // =====================================================
        // PERMANENT ADDRESS
        // =====================================================

        applicant.setPermanentCareOf(
                a.getPermanentCareOf()
        );

        applicant.setPermanentAddressLine1(
                a.getPermanentAddressLine1()
        );

        applicant.setPermanentAddressLine2(
                a.getPermanentAddressLine2()
        );

        applicant.setPermanentLandmark(
                a.getPermanentLandmark()
        );

        applicant.setPermanentPincode(
                a.getPermanentPincode()
        );

        applicant.setPermanentCity(
                a.getPermanentCity()
        );

        applicant.setPermanentState(
                a.getPermanentState()
        );

        applicant.setPermanentPropertyStatus(
                PropertyStatus.valueOf(
                        a.getPermanentPropertyStatus()
                )
        );

        // =====================================================
        // RESIDENCE ADDRESS
        // =====================================================

        applicant.setResidenceAddressLine1(
                a.getResidenceAddressLine1()
        );

        applicant.setResidenceAddressLine2(
                a.getResidenceAddressLine2()
        );

        applicant.setResidenceAddressLine3(
                a.getResidenceAddressLine3()
        );

        applicant.setResidenceLandmark(
                a.getResidenceLandmark()
        );

        applicant.setResidencePincode(
                a.getResidencePincode()
        );

        applicant.setResidenceCity(
                a.getResidenceCity()
        );

        applicant.setResidenceState(
                a.getResidenceState()
        );

        applicant.setResidencePropertyStatus(
                PropertyStatus.valueOf(
                        a.getResidencePropertyStatus()
                )
        );
        applicant.setAreaCategory(
                toAreaCategory(
                        a.getAreaCategory()
                )
        );

        // =====================================================
        // MAILING ADDRESS
        // =====================================================

        applicant.setMailingSameAsResidence(
                a.getMailingSameAsResidence()
        );

        applicant.setMailingSameAsPermanent(
                a.getMailingSameAsPermanent()
        );

        applicant.setMailingAddressLine1(
                a.getMailingAddressLine1()
        );

        applicant.setMailingAddressLine2(
                a.getMailingAddressLine2()
        );

        applicant.setMailingAddressLine3(
                a.getMailingAddressLine3()
        );

        applicant.setMailingLandmark(
                a.getMailingLandmark()
        );

        applicant.setMailingPincode(
                a.getMailingPincode()
        );

        applicant.setMailingCity(
                a.getMailingCity()
        );

        applicant.setMailingState(
                a.getMailingState()
        );

        applicant.setMailingPropertyStatus(
                PropertyStatus.valueOf(
                        a.getMailingPropertyStatus()
                )
        );

        // =====================================================
        // BUSINESS DETAILS
        // =====================================================

        applicant.setBusinessSameAsResidence(
                a.getBusinessSameAsResidence()
        );

        applicant.setBusinessSameAsPermanent(
                a.getBusinessSameAsPermanent()
        );

        applicant.setBusinessName(
                a.getBusinessName()
        );

        if (a.getBusinessType() != null &&
                !a.getBusinessType().isBlank()) {

            applicant.setBusinessType(
                    BusinessType.valueOf(
                            a.getBusinessType()
                    )
            );
        }
        /*If Business Type is mandatory in your LOS:

if (a.getBusinessType() == null ||
    a.getBusinessType().isBlank()) {

    throw new RuntimeException(
            "Business Type is required"
    );
}*/

        applicant.setBusinessAddressLine1(
                a.getBusinessAddressLine1()
        );

        applicant.setBusinessAddressLine2(
                a.getBusinessAddressLine2()
        );

        applicant.setBusinessAddressLine3(
                a.getBusinessAddressLine3()
        );

        applicant.setBusinessReference1Address(
                a.getBusinessReference1Address()
        );

        applicant.setBusinessReference1Contact(
                a.getBusinessReference1Contact()
        );

        applicant.setBusinessReference2Address(
                a.getBusinessReference2Address()
        );

        applicant.setBusinessReference2Contact(
                a.getBusinessReference2Contact()
        );

        applicant.setBusinessLandmark(
                a.getBusinessLandmark()
        );

        applicant.setBusinessPincode(
                a.getBusinessPincode()
        );

        applicant.setBusinessCity(
                a.getBusinessCity()
        );

        applicant.setBusinessState(
                a.getBusinessState()
        );

        applicant.setOfficePropertyStatus(
                PropertyStatus.valueOf(
                        a.getOfficePropertyStatus()
                )
        );

        applicant.setBusinessAreaCategory(
                toAreaCategory(
                        a.getBusinessAreaCategory()
                )
        );

        // =====================================================
        // KYC
        // =====================================================

        applicant.setAadhaarNumber(
                temp.getAadhaar()
                        .getVerifiedAadhaarNumber()
        );

        applicant.setAadhaarName(
                temp.getAadhaar()
                        .getVerifiedName()
        );

        applicant.setAadhaarDob(
                temp.getAadhaar()
                        .getVerifiedDob()
        );

        applicant.setAadhaarGender(
                temp.getAadhaar()
                        .getVerifiedGender()
        );

        applicant.setPanNumber(
                temp.getPan()
                        .getVerifiedPanNumber()
        );

        applicant.setPanName(
                temp.getPan()
                        .getVerifiedName()
        );

        applicant.setVerifiedName(
                temp.getAadhaar()
                        .getVerifiedName()
        );

        applicant.setVerifiedMobileNumber(
                temp.getMobile()
                        .getMobileNumber()
        );

        // =====================================================
        // CREDIT / AML
        // =====================================================

        applicant.setCreditScore(
                a.getCreditScore()
        );

        applicant.setCreditScoreStatus(
                a.getCreditScoreStatus()
        );

        applicant.setAmlScore(
                a.getAmlScore()
        );

        applicant.setAmlName(
                a.getAmlName()
        );

        applicant.setAmlGender(
                a.getAmlGender()
        );

        applicant.setAmlDob(
                a.getAmlDob()
        );

        // =====================================================
        // CLIENT IDS
        // =====================================================

        if (temp.getAadhaar() != null) {
            applicant.setAadhaarClientId(
                    temp.getAadhaar().getClientId()
            );
        }

        if (temp.getPan() != null) {
            applicant.setPanClientId(
                    temp.getPan().getClientId()
            );
        }

        if (temp.getVoter() != null) {
            applicant.setVoterClientId(
                    temp.getVoter().getClientId()
            );
        }

        if (temp.getDl() != null) {
            applicant.setDlClientId(
                    temp.getDl().getClientId()
            );
        }

        if (temp.getMobile() != null) {
            applicant.setMobileClientId(
                    temp.getMobile().getClientId()
            );
        }

        return applicant;
    }

    private String generateApplicantNumber() {

        return "APP-"
                + System.currentTimeMillis();
    }


    public ResumeTempApplicationResponseDTO getResumeData(
            String leadId
    ) {

        TempLoanApplication temp =
                repository.findByLeadId(leadId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "No application found"
                                )
                        );

        return ResumeTempApplicationResponseDTO.builder()
                .tempId(temp.getId())
                .leadId(temp.getLeadId())
                .resume(temp.getIsResume())
                .currentStage(
                        temp.getCurrentStage().name()
                )
                .isKycCompleted(
                        temp.getIsKycCompleted()
                )
                .isApplicantCompleted(
                        temp.getIsApplicantCompleted()
                )
                .isSubmitted(
                        temp.getIsSubmitted()
                )
                .build();
    }

    public TempLoanApplication getTempApplication(
            String tempId
    ) {
        return repository.findById(tempId)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Temp application not found"
                        )
                );
    }



    private PropertyStatus toPropertyStatus(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        try {

            return PropertyStatus.valueOf(
                    value.trim().toUpperCase()
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Invalid Property Status : " + value
            );

        }

    }

    private AreaCategory toAreaCategory(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        try {

            return AreaCategory.valueOf(
                    value.trim().toUpperCase()
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Invalid Area Category : " + value
            );

        }

    }

    private BusinessType toBusinessType(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        try {

            return BusinessType.valueOf(
                    value.trim().toUpperCase()
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Invalid Business Type : " + value
            );

        }

    }




}