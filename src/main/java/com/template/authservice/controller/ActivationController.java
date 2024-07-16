package com.template.authservice.controller;

import com.template.authservice.dto.activation.ActivateRequest;
import com.template.authservice.dto.activation.SendActivationEmailRequest;
import com.template.authservice.service.EmailActivationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/activation")
@RequiredArgsConstructor
public class ActivationController {

    private final EmailActivationService service;

    @PostMapping("/sendEmail")
    public ResponseEntity<?> sendEmail(@Valid @RequestBody SendActivationEmailRequest request) {
        service.sendEmail(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/activate")
    public ResponseEntity<?> activate(@Valid @RequestBody ActivateRequest request) {
        service.activate(request);
        return ResponseEntity.ok().build();
    }

}
