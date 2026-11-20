package com.calorie.management.auth.service;

import com.calorie.management.auth.dto.LoginRequest;
import com.calorie.management.auth.dto.RefreshRequest;
import com.calorie.management.auth.dto.SignUpRequest;
import com.calorie.management.auth.dto.SignUpResponse;
import com.calorie.management.auth.dto.TokenResponse;
import com.calorie.management.auth.refresh.RefreshToken;
import com.calorie.management.auth.refresh.RefreshTokenService;
import com.calorie.management.entity.User;
import com.calorie.management.exception.ConflictException;
import com.calorie.management.repository.UserRepository;
import com.calorie.management.security.properties.JwtProperties;
import com.calorie.management.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final JwtProperties jwtProperties;

    @Transactional
    public TokenResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Bad credentials"));

        return issueTokens(user);
    }

    @Transactional
    public SignUpResponse signup(SignUpRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("An account with this email already exists");
        }
        User savedUser = userRepository.save(User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .build());

        return new SignUpResponse(savedUser.getId(), savedUser.getEmail());
    }

    /** Refresh-token rotation: revoke the presented token and issue a fresh pair. */
    @Transactional
    public TokenResponse refresh(RefreshRequest request) {
        RefreshToken token = refreshTokenService.validate(request.refreshToken());
        refreshTokenService.revoke(token);
        return issueTokens(token.getUser());
    }

    @Transactional
    public void logout(RefreshRequest request) {
        refreshTokenService.revokeIfPresent(request.refreshToken());
    }

    private TokenResponse issueTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        refreshTokenService.storeToken(refreshToken, user, jwtProperties.getRefreshTokenTtlSeconds());

        return new TokenResponse(
                accessToken,
                refreshToken,
                "Bearer",
                jwtProperties.getAccessTokenTtlSeconds()
        );
    }
}
