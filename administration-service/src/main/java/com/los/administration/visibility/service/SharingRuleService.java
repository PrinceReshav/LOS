package com.los.administration.visibility.service;

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
    private final UserVisibilityService userVisibilityService;
    private final UserRepository userRepository;
    private final IncrementalVisibilityService incrementalVisibilityService;

    @Transactional
    public void createRule(SharingRuleRequest req) {

        Role fromRole = roleRepository.findByRoleName(req.getFromRoleName())
                .orElseThrow(() -> new RuntimeException("From role not found"));

        Role toRole = roleRepository.findByRoleName(req.getToRoleName())
                .orElseThrow(() -> new RuntimeException("To role not found"));

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
}