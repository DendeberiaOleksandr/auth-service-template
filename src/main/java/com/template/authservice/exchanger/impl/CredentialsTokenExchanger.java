package com.template.authservice.exchanger.impl;

import com.template.authservice.dto.token.TokenRequest;
import com.template.authservice.exchanger.AbstractTokenExchanger;
import com.template.authservice.model.TokenHandlerType;
import com.template.authservice.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class CredentialsTokenExchanger extends AbstractTokenExchanger {

    public CredentialsTokenExchanger(UserService userService) {
        super(userService);
    }

    @Override
    protected String getUserEmail(TokenRequest request) {
        return request.email();
    }

    @Override
    public TokenHandlerType getTokenHandlerType() {
        return TokenHandlerType.CREDENTIALS;
    }
}
