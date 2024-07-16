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

    @Value("${app.reset.password.maxAttempts:3}")
    private int maxResetPasswordAttempts;

    @Value("${app.reset.password.failedEnterAttempts:3}")
    private int failedEnterAttempts;

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

        publisher.publishEvent(new EmailResetPasswordEvent(this, code, email));
    }

    private void validateResetPassword(User user) {
        LocalDateTime resetPasswordCodeLastSentAt = user.getResetPasswordCodeLastSentAt();
        if (user.getResetPasswordSentTimes() >= maxResetPasswordAttempts &&
                resetPasswordCodeLastSentAt != null && resetPasswordCodeLastSentAt.plusMinutes(5L).isBefore(LocalDateTime.now())) {
            throw new ValidationException("You have exceeded reset password attempts. Please try again after %s"
                    .formatted(resetPasswordCodeLastSentAt.plusMinutes(5L).format(DateTimeFormatter.ISO_DATE_TIME)));
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
        } else {
            int resetPasswordCodeEnteredTimes = user.getInvalidResetPasswordCodeEnteredTimes() + 1;
            user.setResetPasswordSentTimes(resetPasswordCodeEnteredTimes);
            user.setInvalidResetPasswordCodeEnteredLastTimeAt(LocalDateTime.now());
        }

        userService.save(user);
        throw new ValidationException("You have entered an invalid reset password code");
    }

    private void validateChangePassword(User user) {
        int invalidResetPasswordCodeEnteredTimes = user.getInvalidResetPasswordCodeEnteredTimes() + 1;
        LocalDateTime invalidResetPasswordCodeEnteredLastTimeAt = user.getInvalidResetPasswordCodeEnteredLastTimeAt();
        if (invalidResetPasswordCodeEnteredTimes >= failedEnterAttempts &&
                invalidResetPasswordCodeEnteredLastTimeAt != null &&
                invalidResetPasswordCodeEnteredLastTimeAt.plusMinutes(5L).isBefore(LocalDateTime.now())) {
            throw new ValidationException("You have exceeded reset password code entering attempts. Please try again after %s"
                    .formatted(invalidResetPasswordCodeEnteredLastTimeAt.plusMinutes(5L).format(DateTimeFormatter.ISO_DATE_TIME)));
        }
        user.setInvalidResetPasswordCodeEnteredTimes(0);
    }

}
