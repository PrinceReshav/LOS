package com.los.loanoriginatingsystem.systemconfig.service;

import com.los.loanoriginatingsystem.common.exception.ResourceNotFoundException;
import com.los.loanoriginatingsystem.systemconfig.dto.GeneralConfigRequest;
import com.los.loanoriginatingsystem.systemconfig.dto.StampDutyConfigRequest;
import com.los.loanoriginatingsystem.systemconfig.entity.GeneralConfig;
import com.los.loanoriginatingsystem.systemconfig.entity.StampDutyConfig;
import com.los.loanoriginatingsystem.systemconfig.repository.GeneralConfigRepository;
import com.los.loanoriginatingsystem.systemconfig.repository.StampDutyConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SystemConfigService {

    private final StampDutyConfigRepository stampDutyRepository;
    private final GeneralConfigRepository generalConfigRepository;

    // ================= Stamp duty =================

    @Transactional
    public StampDutyConfig createStampDuty(StampDutyConfigRequest request) {
        if (stampDutyRepository.existsByStateCode(request.getStateCode())) {
            throw new IllegalArgumentException("Stamp duty config already exists for state: " + request.getStateCode());
        }
        StampDutyConfig entity = new StampDutyConfig();
        entity.setId(UUID.randomUUID().toString());
        entity.setActive(true);
        applyStampDuty(entity, request);
        return stampDutyRepository.save(entity);
    }

    @Transactional
    public StampDutyConfig updateStampDuty(String id, StampDutyConfigRequest request) {
        StampDutyConfig entity = stampDutyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stamp duty config not found: " + id));
        applyStampDuty(entity, request);
        return stampDutyRepository.save(entity);
    }

    @Transactional
    public StampDutyConfig setStampDutyActive(String id, boolean active) {
        StampDutyConfig entity = stampDutyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stamp duty config not found: " + id));
        entity.setActive(active);
        return stampDutyRepository.save(entity);
    }

    public List<StampDutyConfig> getAllStampDuty() {
        return stampDutyRepository.findAll();
    }

    public StampDutyConfig getStampDutyByState(String stateCode) {
        return stampDutyRepository.findByStateCodeAndActiveTrue(stateCode)
                .orElseThrow(() -> new ResourceNotFoundException("No active stamp duty config for state: " + stateCode));
    }

    private void applyStampDuty(StampDutyConfig entity, StampDutyConfigRequest request) {
        entity.setStateCode(request.getStateCode().trim().toUpperCase());
        entity.setStateName(request.getStateName());
        entity.setStampDutyPercent(request.getStampDutyPercent());
        entity.setFlatFee(request.getFlatFee());
        entity.setDescription(request.getDescription());
    }

    // ================= General config =================

    @Transactional
    public GeneralConfig createGeneralConfig(GeneralConfigRequest request) {
        if (generalConfigRepository.existsByConfigKey(request.getConfigKey())) {
            throw new IllegalArgumentException("General config already exists for key: " + request.getConfigKey());
        }
        GeneralConfig entity = new GeneralConfig();
        entity.setId(UUID.randomUUID().toString());
        entity.setActive(true);
        applyGeneralConfig(entity, request);
        return generalConfigRepository.save(entity);
    }

    @Transactional
    public GeneralConfig updateGeneralConfig(String id, GeneralConfigRequest request) {
        GeneralConfig entity = generalConfigRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("General config not found: " + id));
        applyGeneralConfig(entity, request);
        return generalConfigRepository.save(entity);
    }

    @Transactional
    public GeneralConfig setGeneralConfigActive(String id, boolean active) {
        GeneralConfig entity = generalConfigRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("General config not found: " + id));
        entity.setActive(active);
        return generalConfigRepository.save(entity);
    }

    public List<GeneralConfig> getAllGeneralConfig() {
        return generalConfigRepository.findAll();
    }

    /** Convenience read used by other services (e.g. "AML_SCORE_THRESHOLD"). Returns null if missing/inactive. */
    public String getValue(String key) {
        return generalConfigRepository.findByConfigKeyAndActiveTrue(key)
                .map(GeneralConfig::getConfigValue)
                .orElse(null);
    }

    private void applyGeneralConfig(GeneralConfig entity, GeneralConfigRequest request) {
        entity.setConfigKey(request.getConfigKey().trim());
        entity.setConfigValue(request.getConfigValue());
        entity.setDescription(request.getDescription());
    }
}
