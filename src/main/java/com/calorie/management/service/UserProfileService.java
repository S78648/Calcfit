package com.calorie.management.service;

import com.calorie.management.dto.*;
import com.calorie.management.entity.User;
import com.calorie.management.entity.UserProfile;
import com.calorie.management.entity.UserTarget;
import com.calorie.management.enums.Gender;
import com.calorie.management.enums.GoalType;
import com.calorie.management.exception.InvalidRequestException;
import com.calorie.management.exception.ResourceNotFoundException;
import com.calorie.management.repository.UserProfileRepository;
import com.calorie.management.repository.UserRepository;
import com.calorie.management.repository.UserTargetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserTargetRepository userTargetRepository;
    private final TargetCalculationService targetCalculationService;


    @Transactional
    public UserDashboardResponse saveProfile(
            UUID userId,
            UserProfileRequest request) {

        UserProfile profile = userProfileRepository
                .findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "User not found"));
                    UserProfile p = new UserProfile();
//                    p.setUserId(userId);
                    p.setUser(user);
                    return p;
                });

        applyRequest(profile, request);

        // user_profiles.gender is NOT NULL: fail with 400 instead of a DB constraint 500.
        if (profile.getGender() == null) {
            throw new InvalidRequestException("gender is required when creating a profile");
        }

        UserProfile savedProfile =
                userProfileRepository.save(profile);

        if (isProfileComplete(savedProfile)) {
            recalculateTargets(savedProfile);
        }

        return buildDashboardResponse(userId);
    }


    @Transactional
    public UserDashboardResponse updateProfile(
            UUID userId,
            UserProfileRequest request) {

        UserProfile profile =
                userProfileRepository.findByUserId(userId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Profile not found"));

        applyRequest(profile, request);

        UserProfile savedProfile =
                userProfileRepository.save(profile);

        if (isProfileComplete(savedProfile)) {
            recalculateTargets(savedProfile);
        }

        return buildDashboardResponse(userId);
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public UserDashboardResponse getProfile(UUID userId) {

        userProfileRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Profile not found"));

        return buildDashboardResponse(userId);
    }


    private UserResponse buildUserResponse(
            User user,
            UserProfile profile) {

        return new UserResponse(
                user.getId(),
                profile.getFullName(),
                user.getEmail()
        );
    }

    private UserProfileResponse buildProfileResponse(
            UserProfile profile) {

        return new UserProfileResponse(
                profile.getUserId(),
                profile.getFullName(),
                profile.getAge(),
                profile.getGender(),
                profile.getHeightCm(),
                profile.getWeightKg(),
                profile.getActivityLevel(),
                profile.getGoalType(),
                profile.getTargetWeightKg(),
                profile.getWeeklyPaceKg()
        );
    }

    private UserTargetResponse buildTargetResponse(
            UserTarget target) {

        if (target == null) {
            return null;
        }

        return new UserTargetResponse(
                target.getTargetCalories(),
                target.getTargetProteinGrams(),
                target.getTargetCarbsGrams(),
                target.getTargetFatGrams(),
                target.getTargetFiberGrams(),
                target.getTargetWaterMl()
        );
    }

    /**
     * Adult RDA by sex (values unchanged from before; the age-specific branches returned the
     * same numbers, so they were folded together). Non-binary / unknown use the midpoint defaults.
     * Vitamin B12 is 2.4 mcg for all adults.
     */
    private MicronutrientResponse buildMicronutrientResponse(UserProfile profile) {
        Gender gender = profile.getGender();
        //                  vitA  vitC vitD vitE  B12   Ca    Fe  Mg   Zn  K
        if (gender == Gender.MALE) {
            return micros(900, 90, 15, 15, 2.4, 1000, 8, 420, 11, 3400);
        }
        if (gender == Gender.FEMALE) {
            return micros(700, 75, 15, 15, 2.4, 1000, 18, 320, 8, 2600);
        }
        return micros(800, 82, 15, 15, 2.4, 1000, 12, 370, 9, 3000);
    }

    private static MicronutrientResponse micros(
            double vitA, double vitC, double vitD, double vitE, double b12,
            double calcium, double iron, double magnesium, double zinc, double potassium) {
        return new MicronutrientResponse(
                BigDecimal.valueOf(vitA), BigDecimal.valueOf(vitC), BigDecimal.valueOf(vitD),
                BigDecimal.valueOf(vitE), BigDecimal.valueOf(b12),
                BigDecimal.valueOf(calcium), BigDecimal.valueOf(iron), BigDecimal.valueOf(magnesium),
                BigDecimal.valueOf(zinc), BigDecimal.valueOf(potassium));
    }

    private UserDashboardResponse buildDashboardResponse(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Profile not found"));

        UserTarget target = userTargetRepository.findById(userId)
                .orElse(null);

        MicronutrientResponse micronutrients =
                buildMicronutrientResponse(profile);

        return new UserDashboardResponse(
                buildUserResponse(user, profile),
                buildProfileResponse(profile),
                buildTargetResponse(target),
                micronutrients
        );
    }

    private UserTarget recalculateTargets(UserProfile profile) {

        UserTarget target = userTargetRepository
                .findById(profile.getUserId())
                .orElseGet(() -> {
                    UserTarget t = new UserTarget();
                    t.setUserId(profile.getUserId());
                    return t;
                });

        targetCalculationService.calculate(profile, target);

        return userTargetRepository.save(target);
    }

    private void applyRequest(
            UserProfile profile,
            UserProfileRequest request) {

        if (request.fullName() != null) {
            profile.setFullName(request.fullName().trim());
        }

        if (request.age() != null) {
            profile.setAge(request.age());
        }

        if (request.gender() != null) {
            profile.setGender(request.gender());
        }

        if (request.heightCm() != null) {
            profile.setHeightCm(request.heightCm());
        }

        if (request.weightKg() != null) {
            profile.setWeightKg(request.weightKg());
        }

        if (request.activityLevel() != null) {
            profile.setActivityLevel(request.activityLevel());
        }

        if (request.goalType() != null) {
            profile.setGoalType(request.goalType());
        }

        if (request.targetWeightKg() != null) {
            profile.setTargetWeightKg(request.targetWeightKg());
        }

        if (request.weeklyPaceKg() != null) {
            profile.setWeeklyPaceKg(request.weeklyPaceKg());
        }

        normalizeAndValidateGoal(profile);
    }

    /**
     * Maintenance has no target/pace. For lose/gain the target must sit on the right side of the
     * current weight. Messages use "field: reason" so the client can show them under the input.
     */
    private void normalizeAndValidateGoal(UserProfile profile) {
        GoalType goal = profile.getGoalType();
        if (goal == GoalType.MAINTENANCE) {
            profile.setTargetWeightKg(null);
            profile.setWeeklyPaceKg(null);
            return;
        }
        BigDecimal target = profile.getTargetWeightKg();
        BigDecimal current = profile.getWeightKg();
        if (goal == null || target == null || current == null) {
            return;
        }
        if (goal == GoalType.WEIGHT_LOSS && target.compareTo(current) >= 0) {
            throw new InvalidRequestException("targetWeightKg: must be below your current weight (" + current.stripTrailingZeros().toPlainString() + " kg) to lose weight");
        }
        if (goal == GoalType.WEIGHT_GAIN && target.compareTo(current) <= 0) {
            throw new InvalidRequestException("targetWeightKg: must be above your current weight (" + current.stripTrailingZeros().toPlainString() + " kg) to gain weight");
        }
    }

    private boolean isProfileComplete(UserProfile profile) {
        return profile.getAge() != null
                && profile.getGender() != null
                && profile.getHeightCm() != null
                && profile.getFullName() != null
                && profile.getWeightKg() != null
                && profile.getActivityLevel() != null
                && profile.getGoalType() != null;
    }

}
