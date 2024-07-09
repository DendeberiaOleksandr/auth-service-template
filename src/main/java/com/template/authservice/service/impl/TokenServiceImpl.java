package com.template.authservice.service.impl;

import com.template.authservice.dto.token.TokenRequest;
import com.template.authservice.dto.token.TokenResponse;
import com.template.authservice.entity.User;
import com.template.authservice.exchanger.TokenExchanger;
import com.template.authservice.model.TokenHandlerType;
import com.template.authservice.service.JwtService;
import com.template.authservice.service.TokenService;
import com.template.authservice.service.UserService;
import com.template.authservice.validator.pre.TokenPreValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TokenServiceImpl implements TokenService {

    private final Map<TokenHandlerType, TokenExchanger> tokenExchangerMap;
    private final Map<TokenHandlerType, TokenPreValidator> tokenPreValidatorMap;
    private final JwtService jwtService;

    public TokenServiceImpl(List<TokenExchanger> tokenExchangers,
                            List<TokenPreValidator> tokenPreValidators,
                            JwtService jwtService) {
        this.tokenExchangerMap = tokenExchangers.stream()
                .collect(Collectors.toMap(
                        TokenExchanger::getTokenHandlerType,
                        tokenExchanger -> tokenExchanger
                ));
        this.tokenPreValidatorMap = tokenPreValidators.stream()
                .collect(Collectors.toMap(
                        TokenPreValidator::getTokenHandlerType,
                        tokenPreValidator -> tokenPreValidator
                ));
        this.jwtService = jwtService;
    }

    @Transactional
    @Override
    public TokenResponse getToken(TokenRequest tokenRequest) {
        TokenHandlerType handler = tokenRequest.handler();
        TokenExchanger tokenExchanger = tokenExchangerMap.get(handler);
        User user = tokenExchanger.exchange(tokenRequest);

        Optional.ofNullable(tokenPreValidatorMap.get(handler))
                .ifPresent(tokenPreValidator -> tokenPreValidator.preValidate(user, tokenRequest));

        return jwtService.generateTokens(user);
    }
}
