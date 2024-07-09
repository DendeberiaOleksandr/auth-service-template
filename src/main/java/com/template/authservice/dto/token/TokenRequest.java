package com.template.authservice.dto.token;

import com.template.authservice.model.TokenHandlerType;
import jakarta.validation.constraints.NotNull;

public record TokenRequest(@NotNull TokenHandlerType handler, String email, String password, String token) {
}
