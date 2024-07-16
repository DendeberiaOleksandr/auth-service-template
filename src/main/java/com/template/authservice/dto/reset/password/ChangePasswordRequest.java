package com.template.authservice.dto.reset.password;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(@Email String email, @NotBlank String code, @NotBlank String password) {}
