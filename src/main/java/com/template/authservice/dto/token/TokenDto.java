package com.template.authservice.dto.token;

public record TokenDto(String token, long expiresAt) {
}
