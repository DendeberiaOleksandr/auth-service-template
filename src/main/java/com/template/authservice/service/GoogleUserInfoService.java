package com.template.authservice.service;

import com.template.authservice.dto.google.userinfo.UserInfo;

public interface GoogleUserInfoService {

    UserInfo getUserInfoByAccessToken(String accessToken);

}
