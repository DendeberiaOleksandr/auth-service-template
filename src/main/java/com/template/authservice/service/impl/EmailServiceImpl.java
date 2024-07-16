package com.template.authservice.service.impl;

import com.template.authservice.event.EmailActivationEvent;
import com.template.authservice.event.EmailResetPasswordEvent;
import com.template.authservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    @Value("${spring.mail.username}")
    private String emailFrom;

    private final JavaMailSender javaMailSender;

    @Override
    public void sendActivationEmail(EmailActivationEvent event) {
        String emailTo = event.getEmail();
        sendEmail(
                "Hey %s\nHere is your email activation code:\n%s".formatted(emailTo, event.getCode()),
                "Activate Account", emailTo
        );
    }

    @Override
    public void sendResetPasswordEmail(EmailResetPasswordEvent event) {
        String emailTo = event.getEmail();
        sendEmail(
                "Hey %s\nHere is your reset password code:\n%s".formatted(emailTo, event.getCode()),
                "Reset Password", emailTo
        );
    }

    private void sendEmail(String text, String subject, String emailTo) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailFrom);
        message.setTo(emailTo);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
    }

}
