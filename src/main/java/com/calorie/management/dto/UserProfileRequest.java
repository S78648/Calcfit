package com.calorie.management.dto;

import java.math.BigDecimal;

public record UserProfileRequest(
        Integer age,
        String gender,
        BigDecimal heightCm,
        BigDecimal weightKg,
        String activityLevel,
        String goalType
) {}

