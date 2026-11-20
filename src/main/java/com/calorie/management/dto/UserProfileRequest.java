package com.calorie.management.dto;

import com.calorie.management.enums.ActivityLevel;
import com.calorie.management.enums.Gender;
import com.calorie.management.enums.GoalType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/** All fields optional so the profile can be filled step by step (POST) or patched (PUT). */
public record UserProfileRequest(
        @Size(min = 1, max = 255)
        @Pattern(regexp = ".*\\S.*", message = "must not be blank")
        String fullName,

        @Min(10) @Max(120)
        Integer age,

        Gender gender,

        @DecimalMin("50") @DecimalMax("300")
        BigDecimal heightCm,

        @DecimalMin("20") @DecimalMax("500")
        BigDecimal weightKg,

        ActivityLevel activityLevel,
        GoalType goalType
) {}
