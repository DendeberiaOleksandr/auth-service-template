package com.template.authservice.controller;

import com.template.authservice.dto.reset.password.ChangePasswordRequest;
import com.template.authservice.dto.reset.password.ResetPasswordRequest;
import com.template.authservice.service.ResetPasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/resetPassword")
@RequiredArgsConstructor
@Validated
public class ResetPasswordController {

    private final ResetPasswordService resetPasswordService;

    @PostMapping("/sendEmail")
    public ResponseEntity<?> sendEmail(@Valid @RequestBody ResetPasswordRequest request) {
        resetPasswordService.resetPassword(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/change")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        resetPasswordService.changePassword(request);
        return ResponseEntity.ok().build();
    }

}
