package com.template.authservice.controller;

import com.template.authservice.dto.token.TokenRequest;
import com.template.authservice.dto.token.TokenResponse;
import com.template.authservice.service.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/token")
@Validated
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    @PostMapping
    public ResponseEntity<TokenResponse> getToken(@Valid @RequestBody TokenRequest tokenRequest) {
        TokenResponse token = tokenService.getToken(tokenRequest);
        return ResponseEntity.ok(token);
    }

}
