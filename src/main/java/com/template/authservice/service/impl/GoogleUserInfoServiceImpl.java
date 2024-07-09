package com.template.authservice.service.impl;

import com.template.authservice.dto.google.userinfo.UserInfo;
import com.template.authservice.service.GoogleUserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class GoogleUserInfoServiceImpl implements GoogleUserInfoService {

    private final RestTemplate restTemplate;

    @Override
    public UserInfo getUserInfoByAccessToken(String accessToken) {
        return restTemplate.getForObject("https://www.googleapis.com/oauth2/v3/userinfo?access_token=%s".formatted(accessToken), UserInfo.class);
    }
}
