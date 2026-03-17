package com.calorie.management.auth.dto;

public record LoginRequest(
        String email,
        String password
) {}

