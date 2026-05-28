package com.calorie.management.dto;

import com.calorie.management.enums.ActivityLevel;
import com.calorie.management.enums.Gender;
import com.calorie.management.enums.GoalType;

import java.math.BigDecimal;
import java.util.UUID;

public record UserProfileResponse(
        UUID userId,
        Integer age,
        Gender gender,
        BigDecimal heightCm,
        BigDecimal weightKg,
        ActivityLevel activityLevel,
        GoalType goalType
) {}

