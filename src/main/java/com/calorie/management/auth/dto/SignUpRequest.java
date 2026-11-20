package com.calorie.management.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
        @NotBlank @Email @Size(max = 255)
        String email,

        // BCrypt only uses the first 72 bytes.
        @NotBlank @Size(min = 8, max = 72, message = "must be 8-72 characters")
        String password
) {}
