package com.template.authservice.validator.pre;

import com.template.authservice.dto.token.TokenRequest;
import com.template.authservice.entity.User;
import com.template.authservice.model.TokenHandlerType;

public interface TokenPreValidator {

    void preValidate(User user, TokenRequest tokenRequest);

    TokenHandlerType getTokenHandlerType();

}
