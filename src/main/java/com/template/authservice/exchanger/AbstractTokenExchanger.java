package com.template.authservice.exchanger;

import com.template.authservice.dto.token.TokenRequest;
import com.template.authservice.entity.User;
import com.template.authservice.service.UserService;

public abstract class AbstractTokenExchanger implements TokenExchanger {

    private final UserService userService;

    public AbstractTokenExchanger(UserService userService) {
        this.userService = userService;
    }

    @Override
    public User exchange(TokenRequest request) {
        String userEmail = getUserEmail(request);
        return userService.getByEmail(userEmail);
    }

    protected abstract String getUserEmail(TokenRequest request);
}
