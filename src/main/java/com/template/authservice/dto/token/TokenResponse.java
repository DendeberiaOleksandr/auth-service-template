package com.template.authservice.dto.token;

public record TokenResponse(TokenDto accessToken, TokenDto refreshToken) {
}
