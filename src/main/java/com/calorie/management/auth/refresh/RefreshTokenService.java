package com.calorie.management.auth.refresh;

import com.calorie.management.entity.User;
import com.calorie.management.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    /** Stores only a SHA-256 hash of the token. TTL is in SECONDS. */
    public void storeToken(String refreshToken, User user, long ttlSeconds) {
        LocalDateTime now = LocalDateTime.now();
        RefreshToken token = RefreshToken.builder()
                .tokenHash(hashToken(refreshToken))
                .user(user)
                .revoked(false)
                .createdAt(now)
                .expiresAt(now.plusSeconds(ttlSeconds))
                .build();

        repository.save(token);
    }

    @Transactional
    public RefreshToken validate(String refreshToken) {
        RefreshToken token = repository.findByTokenHash(hashToken(refreshToken))
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (token.isRevoked()) {
            throw new UnauthorizedException("Refresh token revoked");
        }
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException("Refresh token expired");
        }
        return token;
    }

    public void revoke(RefreshToken token) {
        token.setRevoked(true);
        repository.save(token);
    }

    /** Logout must be idempotent: unknown tokens are ignored. */
    @Transactional
    public void revokeIfPresent(String refreshToken) {
        repository.findByTokenHash(hashToken(refreshToken))
                .filter(t -> !t.isRevoked())
                .ifPresent(this::revoke);
    }

    private String hashToken(String token) {
        return DigestUtils.sha256Hex(token);
    }
}
