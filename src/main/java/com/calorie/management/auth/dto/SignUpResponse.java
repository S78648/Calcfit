package com.calorie.management.auth.dto;

import java.util.UUID;

public record SignUpResponse(UUID id, String email) {
}
