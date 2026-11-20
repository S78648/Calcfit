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

        if (gender == Gender.MALE && age != null && age >= 19 && age <= 50) {
            return new MicronutrientResponse(
                    BigDecimal.valueOf(900),
                    BigDecimal.valueOf(90),
                    BigDecimal.valueOf(15),
                    BigDecimal.valueOf(15),
                    BigDecimal.valueOf(1000),
                    BigDecimal.valueOf(8),
                    BigDecimal.valueOf(420),
                    BigDecimal.valueOf(11),
                    BigDecimal.valueOf(3400)
            );
        } else if (gender == Gender.FEMALE && age != null && age >= 19 && age <= 50) {
            return new MicronutrientResponse(
                    BigDecimal.valueOf(700),
                    BigDecimal.valueOf(75),
                    BigDecimal.valueOf(15),
                    BigDecimal.valueOf(15),
                    BigDecimal.valueOf(1000),
                    BigDecimal.valueOf(18),
                    BigDecimal.valueOf(320),
                    BigDecimal.valueOf(8),
                    BigDecimal.valueOf(2600)
            );
        }

        if (gender == Gender.MALE) {
            return new MicronutrientResponse(
                    BigDecimal.valueOf(900),
                    BigDecimal.valueOf(90),
                    BigDecimal.valueOf(15),
                    BigDecimal.valueOf(15),
                    BigDecimal.valueOf(1000),
                    BigDecimal.valueOf(8),
                    BigDecimal.valueOf(420),
                    BigDecimal.valueOf(11),
                    BigDecimal.valueOf(3400)
            );
        } else if (gender == Gender.FEMALE) {
            return new MicronutrientResponse(
                    BigDecimal.valueOf(700),
                    BigDecimal.valueOf(75),
                    BigDecimal.valueOf(15),
                    BigDecimal.valueOf(15),
                    BigDecimal.valueOf(1000),
                    BigDecimal.valueOf(18),
                    BigDecimal.valueOf(320),
                    BigDecimal.valueOf(8),
                    BigDecimal.valueOf(2600)
            );
        }

        return new MicronutrientResponse(
                BigDecimal.valueOf(800),
                BigDecimal.valueOf(82),
                BigDecimal.valueOf(15),
                BigDecimal.valueOf(15),
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(12),
                BigDecimal.valueOf(370),
                BigDecimal.valueOf(9),
                BigDecimal.valueOf(3000)
        );
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
