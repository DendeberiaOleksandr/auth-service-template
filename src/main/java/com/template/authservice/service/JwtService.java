package com.template.authservice.service;

import com.template.authservice.dto.token.TokenResponse;
import com.template.authservice.entity.User;

public interface JwtService {

    TokenResponse generateTokens(User user);

    boolean validateToken(String token);

    String getEmailFromToken(String token);

}
