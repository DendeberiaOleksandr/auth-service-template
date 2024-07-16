package com.template.authservice.service.impl;

import com.template.authservice.generator.CodeGenerator;
import com.template.authservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmailActivationServiceImplTest {

    final CodeGenerator codeGenerator = new CodeGenerator();

    EmailActivationServiceImpl emailActivationService;

    @Mock
    UserService userService;

    @BeforeEach
    void setUp() {
        emailActivationService = new EmailActivationServiceImpl(
                userService,
        );
    }

}