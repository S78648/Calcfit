package com.calorie.management.service;

import com.calorie.management.entity.UserProfile;
import com.calorie.management.entity.UserTarget;
import com.calorie.management.enums.Gender;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class TargetCalculationService {

    public UserTarget calculate(
            UserProfile profile,
            UserTarget target) {

        BigDecimal calories =
                calculateCalories(profile, target);

        BigDecimal protein =
                calculateProtein(profile);

        BigDecimal fat =
                calculateFat(calories);

        BigDecimal carbs =
                calculateCarbs(
                        calories,
                        protein,
                        fat
                );

        BigDecimal fiber =
                calculateFiber(calories);

        BigDecimal water =
                calculateWater(profile);

        target.setUserId(profile.getUserId());

        target.setTargetCalories(calories);
        target.setTargetProteinGrams(protein);
        target.setTargetFatGrams(fat);
        target.setTargetCarbsGrams(carbs);
        target.setTargetFiberGrams(fiber);
        target.setTargetWaterMl(water);

        if (target.getCalculatedAt() == null) {
            target.setCalculatedAt(LocalDateTime.now());
        }

        return target;
    }

    private BigDecimal calculateCalories(UserProfile profile,UserTarget target) {

        double weight = profile.getWeightKg().doubleValue();
        double height = profile.getHeightCm().doubleValue();
        double tdee = getTdee(profile, weight, height,target);

        double calories = tdee + profile.getGoalType().getCalorieAdjustment();

        return BigDecimal.valueOf(calories)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private static double getTdee(UserProfile profile, double weight, double height,UserTarget target) {
        int age = profile.getAge();
        double bmr;

        if (profile.getGender()== Gender.MALE) {
            bmr = 10 * weight + 6.25 * height - 5 * age + 5;
        } else {
            bmr = 10 * weight + 6.25 * height - 5 * age - 161;
        }
         double tdee = bmr * profile.getActivityLevel().getFactor();

        target.setBmr(BigDecimal.valueOf(bmr));
        target.setTdee(BigDecimal.valueOf(tdee));

        return tdee;
    }

    private BigDecimal calculateProtein(UserProfile profile) {
        BigDecimal weight = profile.getWeightKg();
        BigDecimal multiplier =
                profile.getGoalType()
                        .getProteinMultiplier();

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
