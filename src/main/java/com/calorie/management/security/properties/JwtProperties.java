package com.calorie.management.security.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.security.jwt")
@Getter
@Setter
public class JwtProperties {
    private String issuer;
    private String secret;
    private long accessTokenTtlSeconds;
    private long refreshTokenTtlSeconds;
}

