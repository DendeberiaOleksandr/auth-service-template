package com.template.authservice.service;

import com.template.authservice.event.EmailActivationEvent;
import com.template.authservice.event.EmailResetPasswordEvent;

public interface EmailService {
    void sendActivationEmail(EmailActivationEvent event);
    void sendResetPasswordEmail(EmailResetPasswordEvent event);
}
