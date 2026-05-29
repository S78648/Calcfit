package com.calorie.management.controller;

import com.calorie.management.dto.UserDashboardResponse;
import com.calorie.management.dto.UserProfileRequest;
import com.calorie.management.dto.UserProfileResponse;
import com.calorie.management.auth.service.CustomUserDetails;
import com.calorie.management.service.UserProfileService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@Tag(name = "User Profile")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/users/me/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    // ==============================
    // CREATE / MULTI-STEP UPDATE
    // ==============================
    @PostMapping
    public ResponseEntity<UserDashboardResponse> createOrUpdateProfile(
            @Valid  @RequestBody UserProfileRequest request,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {

        UserDashboardResponse response =
                userProfileService.saveProfile(principal.getUser().getId(), request);

        return ResponseEntity.ok(response);
    }

    // ==============================
    // UPDATE (STRICT)
    // ==============================
    @PutMapping
    public ResponseEntity<UserDashboardResponse> updateProfile(
            @Valid @RequestBody UserProfileRequest request,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {

        UserDashboardResponse response =
                userProfileService.updateProfile(principal.getUser().getId(), request);

        return ResponseEntity.ok(response);
    }

    // ==============================
    // GET PROFILE
    // ==============================
    @GetMapping
    public ResponseEntity<UserDashboardResponse> getProfile(
            @AuthenticationPrincipal CustomUserDetails principal
    ) {

        UserDashboardResponse response =
                userProfileService.getProfile(principal.getUser().getId());

        return ResponseEntity.ok(response);
    }
}

