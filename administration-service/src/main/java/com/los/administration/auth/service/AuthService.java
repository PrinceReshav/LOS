package com.los.administration.auth.service;

import com.los.administration.auth.dto.LoginRequest;
import com.los.administration.auth.dto.LoginResponse;
import com.los.administration.auth.dto.PasswordSetupRequest;
import com.los.administration.auth.dto.PasswordSetupResponse;
import com.los.administration.auth.entity.PasswordResetTokenEntity;
import com.los.administration.auth.model.Credential;
import com.los.administration.auth.repository.CredentialRepository;
import com.los.administration.common.exception.BadRequestException;
import com.los.administration.common.exception.ResourceNotFoundException;
import com.los.administration.notification.service.AuthEmailService;
import com.los.administration.role.repository.RoleRepository;
import com.los.administration.user.model.User;
import com.los.administration.user.repository.UserRepository;
import com.los.administration.role.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordTokenService tokenService;
    private final CredentialRepository credentialRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthEmailService authEmailService;
    private final RoleRepository roleRepository;


    // ---------- PASSWORD SETUP ----------
    @Transactional
    public PasswordSetupResponse setupPassword(PasswordSetupRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken)) {
            throw new BadRequestException("PASSWORD_SETUP_NOT_ALLOWED");
        }

        PasswordResetTokenEntity token =
                tokenService.validateToken(request.getToken());

        userRepository.findByUserId(token.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Credential credential = credentialRepository
                .findByUserId(token.getUserId())
                .orElse(
                        Credential.builder()
                                .userId(token.getUserId())
                                .active(true)
                                .passwordSet(false)
                                .build()
                );

        credential.setPasswordHash(
                passwordEncoder.encode(request.getNewPassword())
        );
        credential.setPasswordSet(true);

        credentialRepository.save(credential);
        token.markUsed();

        return new PasswordSetupResponse(
                token.getUserId(),
                "Password created successfully"
        );
    }

    // ---------- LOGIN ----------
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {

        User user = userRepository
                .findByUsernameOrEmail(
                        request.getUsernameOrEmail(),
                        request.getUsernameOrEmail()
                )
                .orElseThrow(() ->
                        new BadRequestException("USER_NOT_FOUND"));

        if (!user.getActive()) {
            throw new BadRequestException("USER_DISABLED");
        }

        Credential credential = credentialRepository
                .findByUserId(user.getUserId())
                .orElseThrow(() ->
                        new BadRequestException("PASSWORD_NOT_SET"));

        if (!passwordEncoder.matches(
                request.getPassword(),
                credential.getPasswordHash()
        )) {
            throw new BadRequestException("INVALID_CREDENTIALS");
        }

        Role role = user.getRole();
        if (role == null) {
            throw new IllegalStateException("ROLE_NOT_FOUND");
        }


        String jwt = jwtService.generateToken(
                user.getUserId(),
                role.getRoleName()   // ✅ ADMIN, SALES, etc
        );


        return LoginResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .role(role.getRoleName())
                .token(jwt)
                .build();
    }

    // ---------- FORGOT PASSWORD (USER) ----------
    @Transactional
    public void initiatePasswordReset(String usernameOrEmail) {

        userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .ifPresent(this::sendPasswordResetLink);
    }

    // ---------- FORGOT PASSWORD (ADMIN) ----------
    @Transactional
    public void adminInitiatePasswordReset(String userId) {

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        sendPasswordResetLink(user);
    }

    // ---------- PRIVATE HELPER ----------
    private void sendPasswordResetLink(User user) {

        String token = tokenService.generateToken(
                user.getUserId(),
                user.getEmail()
        );

        String resetLink =
                "https://los.company.com/password/reset?token=" + token;

        authEmailService.sendPasswordResetEmail(
                user.getEmail(),
                user.getUsername(),
                resetLink
        );
    }



}
