package com.calorie.management.exception;

/** Invalid, expired or revoked credentials (e.g. refresh token). Mapped to 401. */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
