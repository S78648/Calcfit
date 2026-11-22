package com.calorie.management;

import com.calorie.management.entity.UserProfile;
import com.calorie.management.entity.UserTarget;
import com.calorie.management.enums.ActivityLevel;
import com.calorie.management.enums.Gender;
import com.calorie.management.enums.GoalType;
import com.calorie.management.service.TargetCalculationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;


class TargetCalculationServiceTest {

    private TargetCalculationService targetCalculationService;

    @BeforeEach
    void setUp() {
        targetCalculationService =
                new TargetCalculationService();
    }

    @Test
    void shouldCalculateTargetsForWeightGainMale() {

        UserProfile profile = UserProfile.builder()
                .age(26)
                .gender(Gender.MALE)
                .heightCm(BigDecimal.valueOf(168))
                .weightKg(BigDecimal.valueOf(65))
                .activityLevel(ActivityLevel.MODERATE)
                .goalType(GoalType.WEIGHT_GAIN)
                .build();

        UserTarget existingTarget =
                UserTarget.builder().build();

        UserTarget target =
                targetCalculationService.calculate(
                        profile,
                        existingTarget
                );

        assertNotNull(target);

        assertEquals(
                BigDecimal.valueOf(130.00).setScale(2),
                target.getTargetProteinGrams()
        );

        assertEquals(
                BigDecimal.valueOf(2275.00).setScale(2),
                target.getTargetWaterMl()
        );

        assertNotNull(target.getCalculatedAt());
    }

    @Test
    void shouldCalculateTargetsForWeightLossMale() {

        UserProfile profile = UserProfile.builder()
                .age(26)
                .gender(Gender.MALE)
                .heightCm(BigDecimal.valueOf(168))
                .weightKg(BigDecimal.valueOf(65))
                .activityLevel(ActivityLevel.MODERATE)
                .goalType(GoalType.WEIGHT_LOSS)
                .build();

        UserTarget existingTarget =
                UserTarget.builder().build();

        UserTarget target =
                targetCalculationService.calculate(
                        profile,
                        existingTarget
                );

        assertNotNull(target);

        assertEquals(
                BigDecimal.valueOf(143.00).setScale(2),
                target.getTargetProteinGrams()
        );

        assertTrue(
                target.getTargetCalories()
                        .compareTo(BigDecimal.ZERO) > 0
        );
    }

    @Test
    void shouldCalculateTargetsForMaintenance() {

        UserProfile profile = UserProfile.builder()
                .age(26)
                .gender(Gender.MALE)
                .heightCm(BigDecimal.valueOf(168))
                .weightKg(BigDecimal.valueOf(65))
                .activityLevel(ActivityLevel.MODERATE)
                .goalType(GoalType.MAINTENANCE)
                .build();

        UserTarget existingTarget =
                UserTarget.builder().build();

        UserTarget target =
                targetCalculationService.calculate(
                        profile,
                        existingTarget
                );

        assertNotNull(target);

        assertTrue(
                target.getTargetCalories()
                        .compareTo(BigDecimal.ZERO) > 0
        );

        assertEquals(
                BigDecimal.valueOf(117.00).setScale(2),
                target.getTargetProteinGrams()
        );
    }

    @Test
    void shouldCalculateTargetsForFemale() {

        UserProfile profile = UserProfile.builder()
                .age(25)
                .gender(Gender.FEMALE)
                .heightCm(BigDecimal.valueOf(160))
                .weightKg(BigDecimal.valueOf(55))
                .activityLevel(ActivityLevel.ACTIVE)
                .goalType(GoalType.MAINTENANCE)
                .build();

        UserTarget existingTarget =
                UserTarget.builder().build();

        UserTarget target =
                targetCalculationService.calculate(
                        profile,
                        existingTarget
                );

        assertNotNull(target);

        assertTrue(
                target.getTargetCalories()
                        .compareTo(BigDecimal.ZERO) > 0
        );

        assertNotNull(
                target.getTargetWaterMl()
        );
    }

    @Test
    void nonBinaryBmrIsMidpointOfMaleAndFemale() {
        // 30y, 170cm, 70kg, sedentary: base = 700 + 1062.5 - 150 = 1612.5
        java.util.function.Function<Gender, BigDecimal> bmrFor = g -> {
            UserProfile p = UserProfile.builder()
                    .age(30).gender(g)
                    .heightCm(BigDecimal.valueOf(170)).weightKg(BigDecimal.valueOf(70))
                    .activityLevel(ActivityLevel.SEDENTARY).goalType(GoalType.MAINTENANCE)
                    .build();
            return targetCalculationService.calculate(p, UserTarget.builder().build()).getBmr();
        };

        assertEquals(0, BigDecimal.valueOf(1617.5).compareTo(bmrFor.apply(Gender.MALE)));
        assertEquals(0, BigDecimal.valueOf(1451.5).compareTo(bmrFor.apply(Gender.FEMALE)));
        assertEquals(0, BigDecimal.valueOf(1534.5).compareTo(bmrFor.apply(Gender.NON_BINARY)));
    }

    @Test
    void shouldReturnCorrectActivityFactors() {

        assertEquals(
                1.2,
                ActivityLevel.SEDENTARY.getFactor()
        );

        assertEquals(
                1.375,
                ActivityLevel.LIGHT.getFactor()
        );

        assertEquals(
                1.55,
                ActivityLevel.MODERATE.getFactor()
        );

        assertEquals(
                1.725,
                ActivityLevel.ACTIVE.getFactor()
        );

        assertEquals(
                1.9,
                ActivityLevel.VERY_ACTIVE.getFactor()
        );
    }

    @Test
    void shouldReturnCorrectGoalConfigurations() {

        assertEquals(
                BigDecimal.valueOf(2.2),
                GoalType.WEIGHT_LOSS.getProteinMultiplier()
        );

        assertEquals(
                -500,
                GoalType.WEIGHT_LOSS.getCalorieAdjustment()
        );

        assertEquals(
                BigDecimal.valueOf(2.0),
                GoalType.WEIGHT_GAIN.getProteinMultiplier()
        );

        assertEquals(
                500,
                GoalType.WEIGHT_GAIN.getCalorieAdjustment()
        );

        assertEquals(
                BigDecimal.valueOf(1.8),
                GoalType.MAINTENANCE.getProteinMultiplier()
        );

        assertEquals(
                0,
                GoalType.MAINTENANCE.getCalorieAdjustment()
        );
    }

    private UserProfile paceProfile(Gender g, double weight, GoalType goal, Double pace) {
        return UserProfile.builder()
                .age(30).gender(g)
                .heightCm(BigDecimal.valueOf(170)).weightKg(BigDecimal.valueOf(weight))
                .activityLevel(ActivityLevel.SEDENTARY).goalType(goal)
                .weeklyPaceKg(pace == null ? null : BigDecimal.valueOf(pace))
                .build();
    }

    @Test
    void weeklyPaceDrivesTheDailyAdjustment() {
        // male 30y 170cm 70kg sedentary: BMR 1617.5, TDEE 1941.0
        UserTarget loss = targetCalculationService.calculate(paceProfile(Gender.MALE, 70, GoalType.WEIGHT_LOSS, 0.5), UserTarget.builder().build());
        // 1941 - 550 = 1391, below the floor max(BMR 1617.5, 1500) -> 1617.5
        assertEquals(0, BigDecimal.valueOf(1617.50).compareTo(loss.getTargetCalories()));

        UserTarget gain = targetCalculationService.calculate(paceProfile(Gender.MALE, 70, GoalType.WEIGHT_GAIN, 0.25), UserTarget.builder().build());
        assertEquals(0, BigDecimal.valueOf(2216.00).compareTo(gain.getTargetCalories())); // 1941 + 275
    }

    @Test
    void lossAboveTheFloorUsesThePaceFormula() {
        // male 30y 170cm 100kg moderate: BMR 1917.5, TDEE 2972.125; -0.5 kg/wk -> 2972.125 - 550 = 2422.125
        UserProfile p = paceProfile(Gender.MALE, 100, GoalType.WEIGHT_LOSS, 0.5);
        p.setActivityLevel(ActivityLevel.MODERATE);
        assertEquals(0, BigDecimal.valueOf(2422.13).compareTo(targetCalculationService.calculate(p, UserTarget.builder().build()).getTargetCalories()));
    }

    @Test
    void deficitNeverGoesBelowBmr() {
        // male 30y 170cm 100kg sedentary: TDEE 2301 - 550 = 1751 < BMR 1917.5 -> 1917.5
        UserProfile p = paceProfile(Gender.MALE, 100, GoalType.WEIGHT_LOSS, 0.5);
        assertEquals(0, BigDecimal.valueOf(1917.50).compareTo(targetCalculationService.calculate(p, UserTarget.builder().build()).getTargetCalories()));
    }

    @Test
    void femaleFloorIs1200OrBmr() {
        // female 30y 170cm 50kg sedentary: BMR 1251.5, TDEE 1501.8; -1 kg/wk -> 401.8 -> floored to max(1251.5, 1200) = 1251.5
        UserProfile p = paceProfile(Gender.FEMALE, 50, GoalType.WEIGHT_LOSS, 1.0);
        assertEquals(0, BigDecimal.valueOf(1251.50).compareTo(targetCalculationService.calculate(p, UserTarget.builder().build()).getTargetCalories()));
    }

    @Test
    void withoutPaceTheLegacyAdjustmentApplies() {
        // male 30y 170cm 100kg moderate: TDEE 2972.125 - 500 = 2472.125
        UserProfile p = paceProfile(Gender.MALE, 100, GoalType.WEIGHT_LOSS, null);
        p.setActivityLevel(ActivityLevel.MODERATE);
        assertEquals(0, BigDecimal.valueOf(2472.13).compareTo(targetCalculationService.calculate(p, UserTarget.builder().build()).getTargetCalories()));
    }
}
