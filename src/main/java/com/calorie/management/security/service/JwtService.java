package com.calorie.management.security.service;

import com.calorie.management.security.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {

    static final String TYPE_CLAIM = "type";
    static final String ACCESS = "access";
    static final String REFRESH = "refresh";

    private final JwtProperties properties;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(properties.getSecret().getBytes());
    }

    public String generateAccessToken(String email) {
        return buildToken(email, ACCESS, properties.getAccessTokenTtlSeconds());
    }

    public String generateRefreshToken(String email) {
        return buildToken(email, REFRESH, properties.getRefreshTokenTtlSeconds());
    }

    private String buildToken(String email, String type, long ttlSeconds) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(email)
                .setId(UUID.randomUUID().toString())
                .setIssuer(properties.getIssuer())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(ttlSeconds)))
                .claim(TYPE_CLAIM, type)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Returns the subject (email) when the token is a valid, unexpired ACCESS token
     * issued by this server; otherwise null. Refresh tokens are rejected here so they
     * cannot be used as Bearer credentials.
     */
    public String extractEmailFromAccessToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .requireIssuer(properties.getIssuer())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            if (!ACCESS.equals(claims.get(TYPE_CLAIM, String.class))) {
                return null;
            }
            return claims.getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }
}
