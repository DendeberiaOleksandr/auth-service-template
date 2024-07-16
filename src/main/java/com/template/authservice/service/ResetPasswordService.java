package com.template.authservice.service;

import com.template.authservice.dto.reset.password.ChangePasswordRequest;
import com.template.authservice.dto.reset.password.ResetPasswordRequest;

public interface ResetPasswordService {

    void resetPassword(ResetPasswordRequest request);

    void changePassword(ChangePasswordRequest request);

}
