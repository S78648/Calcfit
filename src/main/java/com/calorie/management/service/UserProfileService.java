package com.calorie.management.service;

import com.calorie.management.dto.*;
import com.calorie.management.entity.User;
import com.calorie.management.entity.UserProfile;
import com.calorie.management.entity.UserTarget;
import com.calorie.management.enums.Gender;
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
//        User user = userRepository.findById(userId).orElse(()->{});

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
                profile.getAge(),
                profile.getGender(),
                profile.getHeightCm(),
                profile.getWeightKg(),
                profile.getActivityLevel(),
                profile.getGoalType()
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

    private MicronutrientResponse buildMicronutrientResponse(
            UserProfile profile) {

        Integer age = profile.getAge();
        Gender gender = profile.getGender();

        if (gender == Gender.MALE) {

            if (age >= 19 && age <= 50) {
                return new MicronutrientResponse(
                        BigDecimal.valueOf(900),   // vitaminAMcg
                        BigDecimal.valueOf(90),    // vitaminCMg
                        BigDecimal.valueOf(15),    // vitaminDMcg
                        BigDecimal.valueOf(15),    // vitaminEMg
                        BigDecimal.valueOf(1000),  // calciumMg
                        BigDecimal.valueOf(8),     // ironMg
                        BigDecimal.valueOf(420),   // magnesiumMg
                        BigDecimal.valueOf(11),    // zincMg
                        BigDecimal.valueOf(3400)   // potassiumMg
                );
            }

        } else if (gender==Gender.FEMALE) {

            if (age >= 19 && age <= 50) {
                return new MicronutrientResponse(
                        BigDecimal.valueOf(700),   // vitaminAMcg
                        BigDecimal.valueOf(75),    // vitaminCMg
                        BigDecimal.valueOf(15),    // vitaminDMcg
                        BigDecimal.valueOf(15),    // vitaminEMg
                        BigDecimal.valueOf(1000),  // calciumMg
                        BigDecimal.valueOf(18),    // ironMg
                        BigDecimal.valueOf(320),   // magnesiumMg
                        BigDecimal.valueOf(8),     // zincMg
                        BigDecimal.valueOf(2600)   // potassiumMg
                );
            }
        }

        throw new IllegalArgumentException(
                "No micronutrient recommendation found for age="
                        + age +
                        ", gender=" +
                        gender);
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
            profile.setFullName(request.fullName());
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
