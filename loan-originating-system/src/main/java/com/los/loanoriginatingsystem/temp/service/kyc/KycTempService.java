package com.los.loanoriginatingsystem.temp.service.kyc;

import com.los.loanoriginatingsystem.kyc.aadhaar.dto.*;
import com.los.loanoriginatingsystem.kyc.pan.dto.*;
import com.los.loanoriginatingsystem.kyc.drivinglicense.dto.*;
import com.los.loanoriginatingsystem.kyc.voterid.dto.*;

public interface KycTempService {

    void sendAadhaarOtp(String tempId, AadhaarGenerateOtpResponseDTO dto);

    void verifyAadhaarOtp(String tempId, AadhaarSubmitOtpResponseDTO dto);

    void processAadhaarOCR(String tempId, AadhaarOCRResponseDTO dto);

    void verifyAadhaar(String tempId, AadhaarVerificationResponseDTO dto);

    void processPanOCR(String tempId, PanOCRResponseDTO dto);

    void verifyPan(String tempId, PanVerificationResponseDTO dto);

    void processDlOCR(String tempId, DLOCRResponseDTO dto);

    void verifyDl(String tempId, DLVerificationResponseDTO dto);

    void processVoterOCR(String tempId, VoterIdOCRResponseDTO dto);

    void verifyVoter(String tempId, VoterIdVerificationResponseDTO dto);

    void saveLiveness(String tempId, String base64);
}