package com.calorie.management.service;

import com.calorie.management.entity.UserProfile;
import com.calorie.management.entity.UserTarget;
import com.calorie.management.enums.Gender;
import com.calorie.management.enums.GoalType;
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

        double calories = tdee + dailyAdjustment(profile);

        // Safety floor for deficits: never below BMR nor the common minimum intake.
        if (profile.getGoalType() == GoalType.WEIGHT_LOSS) {
            double floor = Math.max(target.getBmr().doubleValue(), minimumCalories(profile.getGender()));
            calories = Math.max(calories, floor);
        }

        return BigDecimal.valueOf(calories)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /** ~7700 kcal per kg of body weight. */
    static final double KCAL_PER_KG = 7700;

    /**
     * kcal/day added to TDEE. With a weekly pace: pace x 7700 / 7 (0.5 kg/week = 550 kcal/day),
     * signed by the goal. Without one (maintenance, or profiles created before V8): the goal's
     * default +/-500.
     */
    static double dailyAdjustment(UserProfile profile) {
        GoalType goal = profile.getGoalType();
        BigDecimal pace = profile.getWeeklyPaceKg();
        if (goal == GoalType.MAINTENANCE || pace == null) {
            return goal.getCalorieAdjustment();
        }
        double sign = Math.signum(goal.getCalorieAdjustment());
        return sign * pace.doubleValue() * KCAL_PER_KG / 7;
    }

    /** Common clinical minimum daily intake: 1200 kcal (female), 1500 kcal otherwise. */
    static double minimumCalories(Gender gender) {
        return gender == Gender.FEMALE ? 1200 : 1500;
    }

    /** Mifflin-St Jeor: +5 male, -161 female; non-binary uses the midpoint (-78). */
    static int sexConstant(Gender gender) {
        return switch (gender) {
            case MALE -> 5;
            case FEMALE -> -161;
            case NON_BINARY -> -78;
        };
    }

    private static double getTdee(UserProfile profile, double weight, double height,UserTarget target) {
        int age = profile.getAge();
        double bmr = 10 * weight + 6.25 * height - 5 * age + sexConstant(profile.getGender());
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
