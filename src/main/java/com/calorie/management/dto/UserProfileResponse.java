package com.calorie.management.dto;

import com.calorie.management.enums.Gender;

import java.math.BigDecimal;
import java.util.UUID;

public record UserProfileResponse(
        UUID userId,
        Integer age,
        Gender gender,
        BigDecimal heightCm,
        BigDecimal weightKg,
        String activityLevel,
        String goalType
) {}

