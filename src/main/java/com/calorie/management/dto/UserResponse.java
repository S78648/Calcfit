package com.calorie.management.dto;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email
) {}
