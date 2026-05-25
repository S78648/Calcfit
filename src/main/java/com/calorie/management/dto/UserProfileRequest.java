package com.calorie.management.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

public record UserProfileRequest(
        @Min(1)
        Integer age,
        String gender,
        @DecimalMin("1")
        BigDecimal heightCm,
        @DecimalMin("1")
        BigDecimal weightKg,
        String activityLevel,
        String goalType
) {}

