package com.los.loanoriginatingsystem.kyc.async;

import com.los.loanoriginatingsystem.kyc.aadhaar.dto.AadhaarOCRResponseDTO;
import com.los.loanoriginatingsystem.temp.service.kyc.KycTempService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@EnableAsync
public class KycAsyncService {

    private final KycTempService kycTempService;

    @Async
    public void processAadhaarAsync(String tempId, AadhaarOCRResponseDTO dto) {
        kycTempService.processAadhaarOCR(tempId, dto);
    }
}
