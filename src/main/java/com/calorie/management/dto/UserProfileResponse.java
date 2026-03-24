package com.calorie.management.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record UserProfileResponse(
        UUID userId,
        Integer age,
        String gender,
        BigDecimal heightCm,
        BigDecimal weightKg,
        String activityLevel,
        String goalType
) {}

