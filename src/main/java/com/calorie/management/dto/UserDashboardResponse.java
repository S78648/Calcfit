package com.calorie.management.dto;

public record UserDashboardResponse(

        UserResponse user,

        UserProfileResponse profile,

        UserTargetResponse targets,

        MicronutrientResponse micronutrients

) {}
