package com.los.loanoriginatingsystem.kyc.mapper;


import com.los.loanoriginatingsystem.kyc.aadhaar.dto.AadhaarSubmitOtpResponseDTO;
import com.los.loanoriginatingsystem.temp.entity.TempLoanApplication;
import com.los.loanoriginatingsystem.kyc.pan.dto.PanVerificationResponseDTO;
import com.los.loanoriginatingsystem.kyc.drivinglicense.dto.DLVerificationResponseDTO;
import com.los.loanoriginatingsystem.kyc.voterid.dto.VoterIdVerificationResponseDTO;

import org.springframework.stereotype.Component;

@Component
public class KYCResponseMapper {

    // =====================================================
    // 🔥 MAIN ENTRY
    // =====================================================
    public void map(String kycType, Object response, TempLoanApplication temp) {

        if (response == null) {
            throw new RuntimeException("KYC response is null");
        }

        switch (kycType) {

            case "AADHAAR" -> {
                if (!(response instanceof AadhaarSubmitOtpResponseDTO dto)) {
                    throw new RuntimeException("Invalid Aadhaar response");
                }
                mapAadhaar(dto, temp);
            }

            case "PAN" -> {
                if (!(response instanceof PanVerificationResponseDTO dto)) {
                    throw new RuntimeException("Invalid PAN response");
                }
                mapPan(dto, temp);
            }

            case "DL" -> {
                if (!(response instanceof DLVerificationResponseDTO dto)) {
                    throw new RuntimeException("Invalid DL response");
                }
                mapDL(dto, temp);
            }

            case "VOTER" -> {
                if (!(response instanceof VoterIdVerificationResponseDTO dto)) {
                    throw new RuntimeException("Invalid Voter response");
                }
                mapVoter(dto, temp);
            }

            case "LIVENESS" -> temp.getLiveness().setVerified(true);

            default -> throw new RuntimeException("Unsupported KYC type");
        }
    }
    // =====================================================
    // 🪪 AADHAAR
    // =====================================================
    private void mapAadhaar(AadhaarSubmitOtpResponseDTO dto,
                            TempLoanApplication temp) {

        if (dto.getData() == null) {
            throw new RuntimeException("Invalid Aadhaar response");
        }

        var data = dto.getData();
        var aadhaar = temp.getAadhaar();

        aadhaar.setVerified(true);

        aadhaar.setRawAadhaarNumber(data.getAadhaarNumber());
        aadhaar.setVerifiedAadhaarNumber(mask(data.getAadhaarNumber()));

        aadhaar.setVerifiedName(data.getFullName());
        aadhaar.setVerifiedDob(data.getDob());
        aadhaar.setVerifiedGender(data.getGender());

        // Address mapping
        if (data.getAddress() != null) {
            var addr = data.getAddress();

            aadhaar.setHouse(addr.getHouse());
            aadhaar.setStreet(addr.getStreet());
            aadhaar.setDistrict(addr.getDist());
            aadhaar.setState(addr.getState());
            aadhaar.setPincode(addr.getCountry()); // adjust if needed
            aadhaar.setLandmark(addr.getLandmark());
        }
    }

    private String mask(String aadhaar) {
        if (aadhaar == null || aadhaar.length() < 4) {
            return aadhaar;
        }
        return "XXXX-XXXX-" + aadhaar.substring(aadhaar.length() - 4);
    }

    // =====================================================
    // 🧾 PAN
    // =====================================================
    private void mapPan(PanVerificationResponseDTO dto,
                        TempLoanApplication temp) {

        if (dto.getData() == null) {
            throw new RuntimeException("Invalid PAN response");
        }
        var data = dto.getData();
        var pan = temp.getPan();

        pan.setVerified(true);
        pan.setVerifiedPanNumber(data.getPanNumber());
        pan.setVerifiedName(data.getFullName());
    }

    // =====================================================
    // 🚗 DRIVING LICENSE
    // =====================================================
    private void mapDL(DLVerificationResponseDTO dto,
                       TempLoanApplication temp) {

        if (dto.getData() == null) {
            throw new RuntimeException("Invalid Driving License response");
        }
        var data = dto.getData();

        temp.getApplicant().setFirstName(data.getName());
        temp.getApplicant().setDob(data.getDob());
        temp.getApplicant().setGender(data.getGender());
    }

    // =====================================================
    // 🗳️ VOTER ID
    // =====================================================
    private void mapVoter(VoterIdVerificationResponseDTO dto,
                          TempLoanApplication temp) {

        if (dto.getData() == null) {
            throw new RuntimeException("Invalid Voter response");
        }
        var data = dto.getData();

        temp.getApplicant().setFirstName(data.getName());
        temp.getApplicant().setGender(data.getGender());
    }
}