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
                300,
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
}