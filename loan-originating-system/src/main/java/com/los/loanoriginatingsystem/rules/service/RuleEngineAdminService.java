package com.los.loanoriginatingsystem.rules.service;

import com.los.loanoriginatingsystem.common.exception.ResourceNotFoundException;
import com.los.loanoriginatingsystem.rules.dto.RuleCriteriaRequest;
import com.los.loanoriginatingsystem.rules.dto.RuleEngineRequest;
import com.los.loanoriginatingsystem.rules.entity.RuleCriteria;
import com.los.loanoriginatingsystem.rules.entity.RuleEngine;
import com.los.loanoriginatingsystem.rules.repository.RuleCriteriaRepository;
import com.los.loanoriginatingsystem.rules.repository.RuleEngineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Admin editing for the Business Rule / Deviation engine: create, edit,
 * activate/deactivate a rule (RuleEngine) and manage its criteria
 * (RuleCriteria). Equivalent to editing Rule_Engine__c / Rule_Criteria__c
 * records in the old Salesforce org, but through a real API instead of
 * data-loading custom objects.
 *
 * Deactivating a rule here means RuleEngineListener / BusinessRuleExecutor
 * should skip it on the next evaluation (repositories already filter on
 * `active = true` - see RuleEngineRepository.findByObjectApiNameAndActiveTrue).
 */
@Service
@RequiredArgsConstructor
public class RuleEngineAdminService {

    private final RuleEngineRepository ruleEngineRepository;
    private final RuleCriteriaRepository ruleCriteriaRepository;

    // ================= Rule (RuleEngine) CRUD =================

    @Transactional
    public RuleEngine createRule(RuleEngineRequest request) {
        RuleEngine entity = new RuleEngine();
        entity.setId(UUID.randomUUID().toString());
        entity.setActive(true);
        apply(entity, request);
        return ruleEngineRepository.save(entity);
    }

    @Transactional
    public RuleEngine updateRule(String id, RuleEngineRequest request) {
        RuleEngine entity = getRuleOrThrow(id);
        apply(entity, request);
        return ruleEngineRepository.save(entity);
    }

    @Transactional
    public RuleEngine setActive(String id, boolean active) {
        RuleEngine entity = getRuleOrThrow(id);
        entity.setActive(active);
        return ruleEngineRepository.save(entity);
    }

    public RuleEngine getRule(String id) {
        return getRuleOrThrow(id);
    }

    public List<RuleEngine> getAll() {
        return ruleEngineRepository.findAll();
    }

    public List<RuleEngine> getByObjectApiName(String objectApiName) {
        return ruleEngineRepository.findByObjectApiNameAndActiveTrue(objectApiName);
    }

    @Transactional
    public void deleteRule(String id) {
        getRuleOrThrow(id);
        ruleCriteriaRepository.deleteAll(ruleCriteriaRepository.findByRuleEngineId(id));
        ruleEngineRepository.deleteById(id);
    }

    private void apply(RuleEngine entity, RuleEngineRequest request) {
        entity.setName(request.getName());
        entity.setObjectApiName(request.getObjectApiName());
        entity.setIsDefault(request.getIsDefault() != null ? request.getIsDefault() : Boolean.FALSE);
        entity.setOperator(request.getOperator() != null ? request.getOperator() : "AND");
        entity.setCustomLogic(request.getCustomLogic());
        entity.setDeviationLevel(request.getDeviationLevel());
    }

    private RuleEngine getRuleOrThrow(String id) {
        return ruleEngineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Business rule not found: " + id));
    }

    // ================= Criteria CRUD (nested under a rule) =================

    public List<RuleCriteria> getCriteria(String ruleId) {
        return ruleCriteriaRepository.findByRuleEngineIdOrderBySequenceAsc(ruleId);
    }

    @Transactional
    public RuleCriteria addCriteria(String ruleId, RuleCriteriaRequest request) {
        getRuleOrThrow(ruleId); // 404s if the parent rule doesn't exist

        RuleCriteria entity = new RuleCriteria();
        entity.setId(UUID.randomUUID().toString());
        entity.setRuleEngineId(ruleId);
        apply(entity, request);

        return ruleCriteriaRepository.save(entity);
    }

    @Transactional
    public RuleCriteria updateCriteria(String ruleId, String criteriaId, RuleCriteriaRequest request) {
        RuleCriteria entity = ruleCriteriaRepository.findById(criteriaId)
                .filter(c -> ruleId.equals(c.getRuleEngineId()))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Criteria not found: " + criteriaId + " under rule " + ruleId));

        apply(entity, request);
        return ruleCriteriaRepository.save(entity);
    }

    @Transactional
    public void deleteCriteria(String ruleId, String criteriaId) {
        RuleCriteria entity = ruleCriteriaRepository.findById(criteriaId)
                .filter(c -> ruleId.equals(c.getRuleEngineId()))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Criteria not found: " + criteriaId + " under rule " + ruleId));

        ruleCriteriaRepository.delete(entity);
    }

    private void apply(RuleCriteria entity, RuleCriteriaRequest request) {
        entity.setFieldName(request.getFieldName());
        entity.setOperator(request.getOperator());
        entity.setValue(request.getValue());
        entity.setFieldCompare(request.getFieldCompare() != null ? request.getFieldCompare() : Boolean.FALSE);
        entity.setSequence(request.getSequence());
    }
}
