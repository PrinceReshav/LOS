package com.los.loanoriginatingsystem.insurance.service;

import com.los.loanoriginatingsystem.common.exception.ResourceNotFoundException;
import com.los.loanoriginatingsystem.insurance.dto.InsuranceCalculationResponse;
import com.los.loanoriginatingsystem.insurance.dto.InsuranceMatrixRequest;
import com.los.loanoriginatingsystem.insurance.dto.PropertyInsuranceRateRequest;
import com.los.loanoriginatingsystem.insurance.entity.InsuranceMatrix;
import com.los.loanoriginatingsystem.insurance.entity.PropertyInsuranceRate;
import com.los.loanoriginatingsystem.insurance.repository.InsuranceMatrixRepository;
import com.los.loanoriginatingsystem.insurance.repository.PropertyInsuranceRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

/**
 * Admin CRUD + activation for both insurance rate cards, and the premium
 * calculators used at underwriting/sanction time.
 *
 * Equivalent to Salesforce's InsuranceCalculator / PropertyInsuranceCalculator
 * Apex classes, rebuilt against a real (age-banded, not exact-age-only)
 * rate table.
 */
@Service
@RequiredArgsConstructor
public class InsuranceMatrixService {

    private final InsuranceMatrixRepository insuranceMatrixRepository;
    private final PropertyInsuranceRateRepository propertyInsuranceRateRepository;

    // ================= Life / credit insurance matrix CRUD =================

    @Transactional
    public InsuranceMatrix create(InsuranceMatrixRequest request) {
        InsuranceMatrix entity = new InsuranceMatrix();
        entity.setId(UUID.randomUUID().toString());
        apply(entity, request);
        return insuranceMatrixRepository.save(entity);
    }

    @Transactional
    public InsuranceMatrix update(String id, InsuranceMatrixRequest request) {
        InsuranceMatrix entity = insuranceMatrixRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Insurance matrix row not found: " + id));
        apply(entity, request);
        return insuranceMatrixRepository.save(entity);
    }

    @Transactional
    public InsuranceMatrix setActive(String id, boolean active) {
        InsuranceMatrix entity = insuranceMatrixRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Insurance matrix row not found: " + id));
        entity.setActive(active);
        return insuranceMatrixRepository.save(entity);
    }

    public List<InsuranceMatrix> getAll() {
        return insuranceMatrixRepository.findAll();
    }

    private void apply(InsuranceMatrix entity, InsuranceMatrixRequest request) {
        if (request.getMinAge() > request.getMaxAge()) {
            throw new IllegalArgumentException("minAge cannot be greater than maxAge");
        }
        entity.setMinAge(request.getMinAge());
        entity.setMaxAge(request.getMaxAge());
        entity.setTenureMonths(request.getTenureMonths());
        entity.setFlatRate(request.getFlatRate());
        entity.setFlatReducedRate(request.getFlatReducedRate());
        entity.setDescription(request.getDescription());
    }

    // ================= Property insurance rate CRUD =================

    @Transactional
    public PropertyInsuranceRate create(PropertyInsuranceRateRequest request) {
        PropertyInsuranceRate entity = new PropertyInsuranceRate();
        entity.setId(UUID.randomUUID().toString());
        apply(entity, request);
        return propertyInsuranceRateRepository.save(entity);
    }

    @Transactional
    public PropertyInsuranceRate update(String id, PropertyInsuranceRateRequest request) {
        PropertyInsuranceRate entity = propertyInsuranceRateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property insurance rate not found: " + id));
        apply(entity, request);
        return propertyInsuranceRateRepository.save(entity);
    }

    @Transactional
    public PropertyInsuranceRate setActive(String id, boolean active) {
        PropertyInsuranceRate entity = propertyInsuranceRateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property insurance rate not found: " + id));
        entity.setActive(active);
        return propertyInsuranceRateRepository.save(entity);
    }

    public List<PropertyInsuranceRate> getAllPropertyRates() {
        return propertyInsuranceRateRepository.findAll();
    }

    private void apply(PropertyInsuranceRate entity, PropertyInsuranceRateRequest request) {
        entity.setPolicyTenureMonths(request.getPolicyTenureMonths());
        entity.setPercentageIncGst(request.getPercentageIncGst());
        entity.setDescription(request.getDescription());
    }

    // ================= Calculation =================

    /**
     * @param age              applicant's age at application
     * @param tenureMonths     loan tenure in months
     * @param sumAssured       amount the life/credit-shield cover is calculated on
     *                         (typically the approved loan amount)
     * @param propertyValue    collateral/property value (nullable - only secured loans have one)
     * @param usePreferentialRate whether to apply flatReducedRate instead of flatRate
     */
    public InsuranceCalculationResponse calculate(
            int age,
            int tenureMonths,
            BigDecimal sumAssured,
            BigDecimal propertyValue,
            boolean usePreferentialRate
    ) {
        InsuranceMatrix lifeRow = insuranceMatrixRepository
                .findApplicableRate(age, tenureMonths)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No insurance matrix rate configured for age=" + age + ", tenureMonths=" + tenureMonths));

        BigDecimal lifeRate = usePreferentialRate && lifeRow.getFlatReducedRate() != null
                ? lifeRow.getFlatReducedRate()
                : lifeRow.getFlatRate();

        BigDecimal lifePremium = sumAssured
                .multiply(lifeRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal propertyPremium = BigDecimal.ZERO;
        BigDecimal propertyRate = null;

        if (propertyValue != null && propertyValue.compareTo(BigDecimal.ZERO) > 0) {
            PropertyInsuranceRate propertyRow = propertyInsuranceRateRepository
                    .findByPolicyTenureMonthsAndActiveTrue(tenureMonths)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "No property insurance rate configured for tenureMonths=" + tenureMonths));

            propertyRate = propertyRow.getPercentageIncGst();
            propertyPremium = propertyValue
                    .multiply(propertyRate)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        return new InsuranceCalculationResponse(
                lifePremium,
                lifeRate,
                propertyPremium,
                propertyRate,
                lifePremium.add(propertyPremium)
        );
    }
}
