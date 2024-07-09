package com.template.authservice.validator.pre.impl;

import com.template.authservice.dto.token.TokenRequest;
import com.template.authservice.entity.User;
import com.template.authservice.model.TokenHandlerType;
import com.template.authservice.validator.pre.AbstractTokenPreValidator;
import org.springframework.stereotype.Component;

@Component
public class GoogleTokenPreValidator extends AbstractTokenPreValidator {

    @Override
    protected void preValidateImpl(User user, TokenRequest tokenRequest) {

    }

    @Override
    public TokenHandlerType getTokenHandlerType() {
        return TokenHandlerType.GOOGLE;
    }
}
