package com.calorie.management.controller;

import com.calorie.management.dto.UserProfileRequest;
import com.calorie.management.dto.UserProfileResponse;
import com.calorie.management.entity.User;
import com.calorie.management.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/me/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    // ==============================
    // CREATE / MULTI-STEP UPDATE
    // ==============================
    @PostMapping
    public ResponseEntity<UserProfileResponse> createOrUpdateProfile(
            @RequestBody UserProfileRequest request,
            @AuthenticationPrincipal User user
    ) {

        UserProfileResponse response =
                userProfileService.createOrUpdateProfile(user.getId(), request);

        return ResponseEntity.ok(response);
    }

    // ==============================
    // UPDATE (STRICT)
    // ==============================
    @PutMapping
    public ResponseEntity<UserProfileResponse> updateProfile(
            @RequestBody UserProfileRequest request,
            @AuthenticationPrincipal User user
    ) {

        UserProfileResponse response =
                userProfileService.updateProfile(user.getId(), request);

        return ResponseEntity.ok(response);
    }

    // ==============================
    // GET PROFILE
    // ==============================
    @GetMapping
    public ResponseEntity<UserProfileResponse> getProfile(
            @AuthenticationPrincipal User user
    ) {

        UserProfileResponse response =
                userProfileService.getProfile(user.getId());

        return ResponseEntity.ok(response);
    }
}

