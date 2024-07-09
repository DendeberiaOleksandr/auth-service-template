package com.template.authservice.validator.pre.impl;

import com.template.authservice.dto.token.TokenRequest;
import com.template.authservice.entity.User;
import com.template.authservice.model.TokenHandlerType;
import com.template.authservice.validator.pre.AbstractTokenPreValidator;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CredentialsTokenPreValidator extends AbstractTokenPreValidator {

    private final PasswordEncoder passwordEncoder;

    @Override
    protected void preValidateImpl(User user, TokenRequest tokenRequest) {
        if (!passwordEncoder.matches(tokenRequest.password(), user.getPassword())) {
            throw new ValidationException("Invalid email or password provided");
        }
    }

    @Override
    public TokenHandlerType getTokenHandlerType() {
        return TokenHandlerType.CREDENTIALS;
    }
}
