package com.calorie.management.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public enum GoalType {

    WEIGHT_LOSS(
            BigDecimal.valueOf(2.2),
            -500
    ),

    MAINTENANCE(
            BigDecimal.valueOf(1.8),
            0
    ),

    WEIGHT_GAIN(
            BigDecimal.valueOf(2.0),
            300
    );

    private final BigDecimal proteinMultiplier;
    private final int calorieAdjustment;

}
