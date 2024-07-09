package com.template.authservice.service.impl;

import com.template.authservice.dto.signup.SignUpRequest;
import com.template.authservice.entity.User;
import com.template.authservice.service.SignUpService;
import com.template.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignUpServiceImpl implements SignUpService {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Transactional
    @Override
    public void signUp(SignUpRequest signUpRequest) {
        User user = User.builder()
                .email(signUpRequest.email())
                .password(passwordEncoder.encode(signUpRequest.password()))
                .build();
        userService.save(user);
    }
}
