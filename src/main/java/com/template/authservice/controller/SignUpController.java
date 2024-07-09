package com.template.authservice.controller;

import com.template.authservice.dto.signup.SignUpRequest;
import com.template.authservice.service.SignUpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/signUp")
@RequiredArgsConstructor
@Validated
public class SignUpController {

    private final SignUpService signUpService;

    @PostMapping
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest request) {
        signUpService.signUp(request);
        return ResponseEntity.ok().build();
    }

}
