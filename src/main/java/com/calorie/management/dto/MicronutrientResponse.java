package com.calorie.management.dto;

import java.math.BigDecimal;

public record MicronutrientResponse(

        BigDecimal vitaminAMcg,
        BigDecimal vitaminCMg,
        BigDecimal vitaminDMcg,
        BigDecimal vitaminEMg,
        BigDecimal vitaminB12Mcg,

        BigDecimal calciumMg,
        BigDecimal ironMg,
        BigDecimal magnesiumMg,
        BigDecimal zincMg,
        BigDecimal potassiumMg
) {}
