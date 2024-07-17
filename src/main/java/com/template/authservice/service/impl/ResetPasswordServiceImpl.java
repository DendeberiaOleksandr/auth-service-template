package com.template.authservice.service.impl;

import com.template.authservice.dto.reset.password.ChangePasswordRequest;
import com.template.authservice.dto.reset.password.ResetPasswordRequest;
import com.template.authservice.entity.User;
import com.template.authservice.event.EmailResetPasswordEvent;
import com.template.authservice.generator.CodeGenerator;
import com.template.authservice.service.ResetPasswordService;
import com.template.authservice.service.UserService;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ResetPasswordServiceImpl implements ResetPasswordService {

    private final ApplicationEventPublisher publisher;
    private final UserService userService;
    private final CodeGenerator codeGenerator;
    private final PasswordEncoder passwordEncoder;

    private int maxResetPasswordAttempts;
    private int maxFailedCodeEnteringAttempts;

    @Transactional
    @Override
    public void resetPassword(ResetPasswordRequest request) {
        String email = request.email();

        User user = userService.getByEmail(email);
        validateResetPassword(user);

        String code = codeGenerator.generate();
        user.setResetPasswordCode(code);
        user.setResetPasswordCodeLastSentAt(LocalDateTime.now());
        int resetPasswordSentTimes = user.getResetPasswordSentTimes();
        user.setResetPasswordSentTimes(++resetPasswordSentTimes);
        userService.save(user);

        publisher.publishEvent(new EmailResetPasswordEvent(this, email, code));
    }

    private void validateResetPassword(User user) {

        if (user.getResetPasswordSentTimes() >= maxResetPasswordAttempts) {

            LocalDateTime resetPasswordCodeLastSentAt = user.getResetPasswordCodeLastSentAt();
            if (resetPasswordCodeLastSentAt != null && resetPasswordCodeLastSentAt.plusMinutes(5L).isAfter(LocalDateTime.now())) {
                throw new ValidationException("You have exceeded reset password attempts. Please try again after %s"
                        .formatted(resetPasswordCodeLastSentAt.plusMinutes(5L).format(DateTimeFormatter.ISO_DATE_TIME)));
            } else {
                user.setResetPasswordSentTimes(0);
            }

        }

    }

    @Transactional
    @Override
    public void changePassword(ChangePasswordRequest request) {
        User user = userService.getByEmail(request.email());
        validateChangePassword(user);

        if (request.code().equals(user.getResetPasswordCode())) {
            user.setResetPasswordCode(null);
            user.setResetPasswordCodeLastSentAt(null);
            user.setResetPasswordSentTimes(0);
            user.setInvalidResetPasswordCodeEnteredTimes(0);
            user.setInvalidResetPasswordCodeEnteredLastTimeAt(null);
            String newPassword = passwordEncoder.encode(request.password());
            user.setPassword(newPassword);
            userService.save(user);
            return;
        } else {
            int resetPasswordCodeEnteredTimes = user.getInvalidResetPasswordCodeEnteredTimes() + 1;
            user.setInvalidResetPasswordCodeEnteredTimes(resetPasswordCodeEnteredTimes);
            user.setInvalidResetPasswordCodeEnteredLastTimeAt(LocalDateTime.now());
            userService.save(user);
        }

        throw new ValidationException("You have entered an invalid reset password code");
    }

    private void validateChangePassword(User user) {
        int invalidResetPasswordCodeEnteredTimes = user.getInvalidResetPasswordCodeEnteredTimes() + 1;

        if (invalidResetPasswordCodeEnteredTimes >= maxFailedCodeEnteringAttempts) {

            LocalDateTime invalidResetPasswordCodeEnteredLastTimeAt = user.getInvalidResetPasswordCodeEnteredLastTimeAt();
            if (invalidResetPasswordCodeEnteredLastTimeAt != null &&
                    invalidResetPasswordCodeEnteredLastTimeAt.plusMinutes(5L).isAfter(LocalDateTime.now())) {
                throw new ValidationException("You have exceeded reset password code entering attempts. Please try again after %s"
                        .formatted(invalidResetPasswordCodeEnteredLastTimeAt.plusMinutes(5L).format(DateTimeFormatter.ISO_DATE_TIME)));
            } else {
                user.setInvalidResetPasswordCodeEnteredTimes(0);
            }

        }
    }

    @Value("${app.reset.password.maxResetPasswordAttempts:3}")
    public void setMaxResetPasswordAttempts(int maxResetPasswordAttempts) {
        this.maxResetPasswordAttempts = maxResetPasswordAttempts;
    }

    @Value("${app.reset.password.maxFailedCodeEnteringAttempts:3}")
    public void setMaxFailedCodeEnteringAttempts(int maxFailedCodeEnteringAttempts) {
        this.maxFailedCodeEnteringAttempts = maxFailedCodeEnteringAttempts;
    }

}
