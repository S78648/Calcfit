package com.calorie.management.auth.refresh;



import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import com.calorie.management.entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    public void storeToken(String refreshToken, User user, long expirationMillis) {

        String hash = hashToken(refreshToken);

        RefreshToken token = RefreshToken.builder()
                .tokenHash(hash)
                .user(user)
                .revoked(false)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusSeconds(expirationMillis / 1000))
                .build();

        repository.save(token);
    }

    @Transactional
    public RefreshToken validate(String refreshToken) {

        String hash = hashToken(refreshToken);

        RefreshToken token = repository.findByTokenHash(hash)
                .orElseThrow(() ->
                        new RuntimeException("Refresh token not found"));

        if (token.isRevoked()) {
            throw new RuntimeException("Refresh token revoked");
        }

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        return token;
    }

    public void revoke(String refreshToken) {

        String hash = hashToken(refreshToken);

        RefreshToken token = repository.findByTokenHash(hash)
                .orElseThrow(() ->
                        new RuntimeException("Token not found"));

        token.setRevoked(true);

        repository.save(token);
    }

    private String hashToken(String token) {
        return DigestUtils.sha256Hex(token);
    }
}

