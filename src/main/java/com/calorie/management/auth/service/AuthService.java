package com.calorie.management.auth.service;


import com.calorie.management.auth.dto.LoginRequest;
import com.calorie.management.auth.dto.RefreshRequest;
import com.calorie.management.auth.dto.SignUpResponse;
import com.calorie.management.auth.dto.TokenResponse;
import com.calorie.management.auth.refresh.RefreshTokenService;
import com.calorie.management.auth.refresh.RefreshToken;
import com.calorie.management.entity.User;
import com.calorie.management.entity.UserProfile;
import com.calorie.management.repository.UserProfileRepository;
import com.calorie.management.repository.UserRepository;
import com.calorie.management.security.properties.JwtProperties;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.calorie.management.security.service.JwtService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final JwtProperties jwtProperties;
    private final UserProfileRepository userProfileRepository;

    public TokenResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken = jwtService.generateAccessToken(user.getEmail());

        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        refreshTokenService.storeToken(
                refreshToken,
                user,
                jwtProperties.getRefreshExpiration()
        );

        return new TokenResponse(
                accessToken,
                refreshToken,
                "Bearer",
                jwtProperties.getExpiration()
        );
    }


    @Transactional
    public SignUpResponse signup(LoginRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Username already exists");
        }
        User savedUser = userRepository.save(User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .build());

        return new SignUpResponse(savedUser.getId(), savedUser.getEmail());
    }

    public TokenResponse refresh(RefreshRequest request) {

        RefreshToken token =
                refreshTokenService.validate(request.refreshToken());

        String email = token.getUser().getEmail();

        String newAccessToken =
                jwtService.generateAccessToken(email);

        return new TokenResponse(
                newAccessToken,
                request.refreshToken(),
                "Bearer",
                jwtProperties.getExpiration()
        );
    }

    @PostMapping("/logout")
    public void logout(@RequestBody RefreshRequest request) {
        refreshTokenService.revoke(request.refreshToken());
    }



}

