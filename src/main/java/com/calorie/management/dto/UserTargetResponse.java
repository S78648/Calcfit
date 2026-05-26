package com.calorie.management.dto;

import java.math.BigDecimal;

public record UserTargetResponse(
        BigDecimal caloriesKcal,
        BigDecimal proteinGrams,
        BigDecimal carbsGrams,
        BigDecimal fatGrams,
        BigDecimal fiberGrams,
        BigDecimal waterMl
) {}
