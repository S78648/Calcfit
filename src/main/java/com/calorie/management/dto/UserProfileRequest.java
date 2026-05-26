package com.calorie.management.dto;

import com.calorie.management.enums.Gender;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

public record UserProfileRequest(
        String fullName,
        @Min(1)
        Integer age,
        Gender gender,
        @DecimalMin("1")
        BigDecimal heightCm,
        @DecimalMin("1")
        BigDecimal weightKg,
        String activityLevel,
        String goalType
) {}

