package com.calorie.management.security.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class BearerTokenExtractor implements TokenExtractor {

    private static final String AUTH_HEADER = "Authorization";
    private static final String PREFIX = "Bearer ";

    @Override
    public String extract(HttpServletRequest request) {

        String header = request.getHeader(AUTH_HEADER);

        if (header == null || !header.startsWith(PREFIX)) {
            return null;
        }

        return header.substring(PREFIX.length());
    }
}

