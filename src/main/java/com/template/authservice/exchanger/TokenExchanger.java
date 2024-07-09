package com.template.authservice.exchanger;

import com.template.authservice.dto.token.TokenRequest;
import com.template.authservice.entity.User;
import com.template.authservice.model.TokenHandlerType;

public interface TokenExchanger {

    User exchange(TokenRequest request);

    TokenHandlerType getTokenHandlerType();

}
