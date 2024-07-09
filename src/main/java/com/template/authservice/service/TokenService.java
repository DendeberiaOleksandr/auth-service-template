package com.template.authservice.service;

import com.template.authservice.dto.token.TokenRequest;
import com.template.authservice.dto.token.TokenResponse;

public interface TokenService {

    TokenResponse getToken(TokenRequest tokenRequest);

}
