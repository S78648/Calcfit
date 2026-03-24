package com.calorie.management.service;

import com.calorie.management.dto.UserProfileRequest;
import com.calorie.management.dto.UserProfileResponse;
import com.calorie.management.entity.UserProfile;
import com.calorie.management.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;


    public UserProfileResponse createOrUpdateProfile(UUID userId, UserProfileRequest request) {
        return upsertProfile(userId, request, true);
    }

    public UserProfileResponse updateProfile(UUID userId, UserProfileRequest request) {
        return upsertProfile(userId, request, false);
    }


    private UserProfileResponse upsertProfile(UUID userId,
                                              UserProfileRequest request,
                                              boolean allowCreate) {

        UserProfile profile = userProfileRepository.findById(userId)
                .orElseGet(() -> {
                    if (!allowCreate) {
                        throw new RuntimeException("Profile not found");
                    }
                    UserProfile newProfile = new UserProfile();
                    newProfile.setUserId(userId);
                    return newProfile;
                });

        // Capture state BEFORE update (for diff logic)
        UserProfile snapshot = cloneProfile(profile);

        // Apply patch
        updateEntity(profile, request);

        // Validate AFTER applying patch but BEFORE saving
        validate(profile, request);

        userProfileRepository.save(profile);

        // Decide recalculation
        boolean isNowComplete = isComplete(profile);
        boolean wasComplete = isComplete(snapshot);

        boolean shouldRecalculate =
                (isNowComplete && !wasComplete) ||  // became complete
                        hasCriticalChanges(snapshot, request); // or critical update

        if (shouldRecalculate) {
            calculateTargets(profile);
            regenerateTodayPlan(userId);
        }

        return toResponse(profile);
    }


    public UserProfileResponse getProfile(UUID userId) {

        UserProfile profile = userProfileRepository.findByUserId(userId).orElse(null);

        assert profile != null;
        return toResponse(profile);
    }

    private UserProfile cloneProfile(UserProfile p) {
        UserProfile clone = new UserProfile();
        clone.setUserId(p.getUserId());
        clone.setAge(p.getAge());
        clone.setGender(p.getGender());
        clone.setHeightCm(p.getHeightCm());
        clone.setWeightKg(p.getWeightKg());
        clone.setActivityLevel(p.getActivityLevel());
        clone.setGoalType(p.getGoalType());
        return clone;
    }


    private boolean hasCriticalChanges(UserProfile oldProfile,
                                       UserProfileRequest request) {

        return (request.weightKg() != null &&
                !request.weightKg().equals(oldProfile.getWeightKg())) ||

                (request.heightCm() != null &&
                        !request.heightCm().equals(oldProfile.getHeightCm())) ||

                (request.activityLevel() != null &&
                        !request.activityLevel().equals(oldProfile.getActivityLevel())) ||

                (request.goalType() != null &&
                        !request.goalType().equals(oldProfile.getGoalType()));
    }




    private boolean isComplete(UserProfile p) {
        return p.getAge() != null &&
                p.getGender() != null &&
                p.getHeightCm() != null &&
                p.getWeightKg() != null &&
                p.getActivityLevel() != null &&
                p.getGoalType() != null;
    }


    private void validate(UserProfile profile, UserProfileRequest request) {

        if (request.age() != null && request.age() <= 0) {
            throw new IllegalArgumentException("Invalid age");
        }

        if (request.heightCm() != null && request.heightCm().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid height");
        }

        if (request.weightKg() != null && request.weightKg().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid weight");
        }
    }




    public void updateEntity(UserProfile profile, UserProfileRequest request) {

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
    }


    public UserProfileResponse toResponse(UserProfile profile) {
        return new UserProfileResponse(
                profile.getUserId(),
                profile.getAge(),
                profile.getGender(),
                profile.getHeightCm(),
                profile.getWeightKg(),
                profile.getActivityLevel(),
                profile.getGoalType()
        );
    }

    private void calculateTargets(UserProfile profile) {

        // Basic BMR (Mifflin-St Jeor)
        BigDecimal weight = profile.getWeightKg();
        BigDecimal height = profile.getHeightCm();
        int age = profile.getAge();

        double bmr;

        if (profile.getGender().equalsIgnoreCase("MALE")) {
            bmr = 10 * weight.doubleValue() +
                    6.25 * height.doubleValue() -
                    5 * age + 5;
        } else {
            bmr = 10 * weight.doubleValue() +
                    6.25 * height.doubleValue() -
                    5 * age - 161;
        }

        // Activity multiplier (simplified)
        double activityFactor = switch (profile.getActivityLevel()) {
            case "SEDENTARY" -> 1.2;
            case "LIGHT" -> 1.375;
            case "MODERATE" -> 1.55;
            case "ACTIVE" -> 1.725;
            default -> 1.2;
        };

        double tdee = bmr * activityFactor;

        // Goal adjustment
        double targetCalories = switch (profile.getGoalType()) {
            case "WEIGHT_LOSS" -> tdee - 500;
            case "WEIGHT_GAIN" -> tdee + 300;
            default -> tdee;
        };

        // 👉 For now just log / store later
        System.out.println("Target Calories: " + targetCalories);

        // Later you will persist this in a "user_targets" table
    }

    private void regenerateTodayPlan(UUID userId) {

        // Step 1: Clear existing plan (if any)
        System.out.println("Deleting old diet plan for user: " + userId);

        // Step 2: Create new plan (dummy logic for now)
        System.out.println("Generating new diet plan...");

        // Example (later this will be DB-driven):
        System.out.println("Breakfast → Oats");
        System.out.println("Lunch → Rice + Chicken");
        System.out.println("Dinner → Salad + Paneer");

        // Later:
        // - Fetch FoodItems by category
        // - Allocate calories per meal
        // - Store in diet_plans table
    }




}
