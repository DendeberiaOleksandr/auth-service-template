package com.template.authservice.service.impl;

import com.template.authservice.event.EmailActivationEvent;
import com.template.authservice.event.EmailResetPasswordEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/*
This test class is disabled as it needs real user credentials. Use it for manual testing.
To use it change USERNAME and PASSWORD to corresponding values
 */
@Disabled
@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    private static final String USERNAME = "username";
    private static final String PASSWORD = "username";

    EmailServiceImpl emailService;

    @Disabled
    @BeforeEach
    void setUp() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

        javaMailSender.setHost("smtp.gmail.com");
        javaMailSender.setPort(587);

        javaMailSender.setUsername(USERNAME);
        javaMailSender.setPassword(PASSWORD);

        Properties props = javaMailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        emailService = new EmailServiceImpl(javaMailSender);
    }

    @Disabled
    @Test
    void sendConfirmationEmail() {
        // given
        final String email = "email@gmail.com";
        final String code = "3492";
        EmailActivationEvent event = new EmailActivationEvent(this, code, email);

        // when
        assertDoesNotThrow(() -> emailService.sendActivationEmail(event));

        // then
    }

    @Test
    void sendResetPasswordEmail() {
        // given
        final String email = "email@gmail.com";
        final String code = "3492";
        EmailResetPasswordEvent event = new EmailResetPasswordEvent(this, code, email);

        // when
        assertDoesNotThrow(() -> emailService.sendResetPasswordEmail(event));

        // then
    }

}
