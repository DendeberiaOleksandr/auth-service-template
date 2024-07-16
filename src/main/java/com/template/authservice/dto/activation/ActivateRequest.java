package com.template.authservice.dto.activation;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ActivateRequest(@Email String email, @NotBlank String code) {}
