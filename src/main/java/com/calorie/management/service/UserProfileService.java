package com.calorie.management.service;

import com.calorie.management.dto.UserProfileRequest;
import com.calorie.management.dto.UserProfileResponse;
import com.calorie.management.entity.UserProfile;
import com.calorie.management.entity.UserTarget;
import com.calorie.management.exception.ResourceNotFoundException;
import com.calorie.management.repository.UserProfileRepository;
import com.calorie.management.repository.UserTargetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserTargetRepository userTargetRepository;
    private final TargetCalculationService targetCalculationService;


    public UserProfileResponse saveProfile(
            UUID userId,
            UserProfileRequest request) {

        UserProfile profile = userProfileRepository
                .findByUserId(userId)
                .orElseGet(() -> {
                    UserProfile p = new UserProfile();
                    p.setUserId(userId);
                    return p;
                });

        applyRequest(profile, request);

        UserProfile savedProfile =
                userProfileRepository.save(profile);

        if (isProfileComplete(savedProfile)) {
            recalculateTargets(savedProfile);
        }

        return toResponse(savedProfile);
    }

    public UserProfileResponse updateProfile(
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

        return toResponse(savedProfile);
    }

    public UserProfileResponse getProfile(UUID userId) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        return toResponse(profile);
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

    private void recalculateTargets(UserProfile profile) {

        UserTarget calculatedTarget =
                targetCalculationService.calculate(profile);

        userTargetRepository.save(calculatedTarget);
    }

    private void applyRequest(
            UserProfile profile,
            UserProfileRequest request) {

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
                && profile.getWeightKg() != null
                && profile.getActivityLevel() != null
                && profile.getGoalType() != null;
    }

}
