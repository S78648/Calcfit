package com.calorie.management.service;

import com.calorie.management.entity.UserProfile;
import com.calorie.management.entity.UserTarget;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class TargetCalculationService {

    public UserTarget calculate(UserProfile profile) {

        BigDecimal calories = calculateCalories(profile);

        BigDecimal protein = calculateProtein(profile);
        BigDecimal fat = calculateFat(calories);
        BigDecimal carbs = calculateCarbs(
                calories,
                protein,
                fat
        );

        BigDecimal fiber = calculateFiber(calories);

        BigDecimal water = calculateWater(profile);

        return UserTarget.builder()
                .userId(profile.getUserId())
                .targetCalories(calories)
                .targetProteinGrams(protein)
                .targetFatGrams(fat)
                .targetCarbsGrams(carbs)
                .targetFiberGrams(fiber)
                .targetWaterMl(water)
                .build();
    }

    private BigDecimal calculateCalories(UserProfile profile) {

        double weight = profile.getWeightKg().doubleValue();
        double height = profile.getHeightCm().doubleValue();
        double tdee = getTdee(profile, weight, height);

        double calories = switch (profile.getGoalType()) {
            case "WEIGHT_LOSS" -> tdee - 500;
            case "WEIGHT_GAIN" -> tdee + 300;
            case "MAINTENANCE" -> tdee;
            default -> throw new IllegalArgumentException(
                    "Invalid goal type");
        };

        return BigDecimal.valueOf(calories)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private static double getTdee(UserProfile profile, double weight, double height) {
        int age = profile.getAge();

        double bmr;

        if ("MALE".equalsIgnoreCase(profile.getGender())) {
            bmr = 10 * weight + 6.25 * height - 5 * age + 5;
        } else {
            bmr = 10 * weight + 6.25 * height - 5 * age - 161;
        }

        double activityFactor = switch (profile.getActivityLevel()) {
            case "SEDENTARY" -> 1.2;
            case "LIGHT" -> 1.375;
            case "MODERATE" -> 1.55;
            case "ACTIVE" -> 1.725;
            case "VERY_ACTIVE" -> 1.9;
            default -> throw new IllegalArgumentException(
                    "Invalid activity level");
        };

        return bmr * activityFactor;
    }

    private BigDecimal calculateProtein(UserProfile profile) {

        BigDecimal weight = profile.getWeightKg();

        BigDecimal multiplier = switch (profile.getGoalType()) {

            case "WEIGHT_LOSS" ->
                    BigDecimal.valueOf(2.2);

            case "WEIGHT_GAIN" ->
                    BigDecimal.valueOf(2.0);

            default ->
                    BigDecimal.valueOf(1.8);
        };

        return weight.multiply(multiplier)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateFat(BigDecimal calories) {

        BigDecimal fatCalories =
                calories.multiply(BigDecimal.valueOf(0.25));

        return fatCalories
                .divide(BigDecimal.valueOf(9),
                        2,
                        RoundingMode.HALF_UP);
    }

    private BigDecimal calculateCarbs(
            BigDecimal calories,
            BigDecimal protein,
            BigDecimal fat) {

        BigDecimal proteinCalories =
                protein.multiply(BigDecimal.valueOf(4));

        BigDecimal fatCalories =
                fat.multiply(BigDecimal.valueOf(9));

        BigDecimal carbCalories =
                calories
                        .subtract(proteinCalories)
                        .subtract(fatCalories);

        return carbCalories.divide(
                BigDecimal.valueOf(4),
                2,
                RoundingMode.HALF_UP
        );
    }

    private BigDecimal calculateFiber(
            BigDecimal calories) {

        return calories
                .multiply(BigDecimal.valueOf(14))
                .divide(BigDecimal.valueOf(1000),
                        2,
                        RoundingMode.HALF_UP);
    }

    private BigDecimal calculateWater(
            UserProfile profile) {

        return profile.getWeightKg()
                .multiply(BigDecimal.valueOf(35))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
