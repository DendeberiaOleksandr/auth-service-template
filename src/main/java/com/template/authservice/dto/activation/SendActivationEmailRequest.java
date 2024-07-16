package com.template.authservice.dto.activation;

import jakarta.validation.constraints.Email;

public record SendActivationEmailRequest(@Email String email) {}
