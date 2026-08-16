package com.los.administration.visibility.service;

import com.los.administration.common.exception.ResourceNotFoundException;
import com.los.administration.role.model.Role;
import com.los.administration.role.repository.RoleRepository;
import com.los.administration.user.model.User;
import com.los.administration.user.repository.UserRepository;
import com.los.administration.visibility.dto.SharingRuleRequest;
import com.los.administration.visibility.model.AccessType;
import com.los.administration.visibility.model.SharingRule;
import com.los.administration.visibility.repository.SharingRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SharingRuleService {

    private final SharingRuleRepository repository;
    private final RoleRepository roleRepository;
    // FIX: userVisibilityService was injected but never called - dead
    // weight. incrementalVisibilityService.onUserCreated(...) now
    // correctly delegates into UserVisibilityService.rebuildVisibilityForUser
    // (which reads this very SharingRule table), so this dependency is
    // no longer needed here at all.
    private final UserRepository userRepository;
    private final IncrementalVisibilityService incrementalVisibilityService;

    @Transactional
    public void createRule(SharingRuleRequest req) {

        Role fromRole = roleRepository.findByRoleName(req.getFromRoleName())
                .orElseThrow(() -> new ResourceNotFoundException("From role not found: " + req.getFromRoleName()));

        Role toRole = roleRepository.findByRoleName(req.getToRoleName())
                .orElseThrow(() -> new ResourceNotFoundException("To role not found: " + req.getToRoleName()));

        SharingRule rule = SharingRule.builder()
                .fromRoleId(fromRole.getRoleId())
                .toRoleId(toRole.getRoleId())
                .accessType(AccessType.RULE)
                .active(true)
                .build();

        repository.save(rule);

        // ✅ rebuild for ALL users of that role
        List<User> affectedUsers = userRepository.findAll().stream()
                .filter(u -> u.getRole().getRoleId().equals(fromRole.getRoleId()))
                .toList();

        for (User user : affectedUsers) {
            incrementalVisibilityService.onUserCreated(user);
        }
    }

    @Transactional(readOnly = true)
    public List<SharingRule> getAll() {
        return repository.findAll();
    }
}