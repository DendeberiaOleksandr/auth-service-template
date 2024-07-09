package com.template.authservice.exchanger.impl;

import com.template.authservice.dto.google.userinfo.UserInfo;
import com.template.authservice.dto.token.TokenRequest;
import com.template.authservice.exchanger.AbstractTokenExchanger;
import com.template.authservice.model.TokenHandlerType;
import com.template.authservice.service.GoogleUserInfoService;
import com.template.authservice.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class GoogleTokenExchanger extends AbstractTokenExchanger {

    private final GoogleUserInfoService googleUserInfoService;

    public GoogleTokenExchanger(GoogleUserInfoService googleUserInfoService,
                                UserService userService) {
        super(userService);
        this.googleUserInfoService = googleUserInfoService;
    }

    @Override
    protected String getUserEmail(TokenRequest request) {
        UserInfo userInfo = googleUserInfoService.getUserInfoByAccessToken(request.token());
        return userInfo.email();
    }

    @Override
    public TokenHandlerType getTokenHandlerType() {
        return TokenHandlerType.GOOGLE;
    }
}
