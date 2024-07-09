package com.template.authservice.dto.signup;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record SignUpRequest(@Email @NotBlank String email, @Min(8L) String password) {
}
