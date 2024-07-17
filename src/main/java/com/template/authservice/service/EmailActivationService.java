package com.template.authservice.service;

import com.template.authservice.dto.activation.ActivationRequest;
import com.template.authservice.dto.activation.SendActivationEmailRequest;

public interface EmailActivationService {

    void sendEmail(SendActivationEmailRequest request);
    void activate(ActivationRequest request);

}
