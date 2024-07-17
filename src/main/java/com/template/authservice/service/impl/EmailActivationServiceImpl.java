package com.template.authservice.service.impl;

import com.template.authservice.dto.activation.ActivationRequest;
import com.template.authservice.dto.activation.SendActivationEmailRequest;
import com.template.authservice.entity.User;
import com.template.authservice.entity.UserStatus;
import com.template.authservice.event.EmailActivationEvent;
import com.template.authservice.generator.CodeGenerator;
import com.template.authservice.service.EmailActivationService;
import com.template.authservice.service.UserService;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class EmailActivationServiceImpl implements EmailActivationService {

    private final UserService userService;
    private final CodeGenerator generator;
    private final ApplicationEventPublisher publisher;

    private int maxActivationEmailAttempts;
    private int maxFailedCodeEnteringAttempts;

    @Transactional
    @Override
    public void sendEmail(SendActivationEmailRequest request) {
        String email = request.email();

        User user = userService.getByEmail(email);
        validateActivationEmail(user);

        String code = generator.generate();
        user.setActivationCode(code);
        int confirmationCodeSentTimes = user.getActivationCodeSentTimes();
        user.setActivationCodeSentTimes(++confirmationCodeSentTimes);
        user.setActivationCodeLastSentAt(LocalDateTime.now());
        userService.save(user);

        publisher.publishEvent(new EmailActivationEvent(this, code, email));
    }

    private void validateActivationEmail(User user) {
        if (user.getActivationCodeSentTimes() >= maxActivationEmailAttempts) {

            LocalDateTime confirmationCodeLastSentAt = user.getActivationCodeLastSentAt();

            if (confirmationCodeLastSentAt != null && confirmationCodeLastSentAt.plusMinutes(5L).isAfter(LocalDateTime.now())) {
                throw new ValidationException("You have exceeded confirmation email send attempts. Please try again after %s"
                        .formatted(confirmationCodeLastSentAt.plusMinutes(5L).format(DateTimeFormatter.ISO_DATE_TIME)));
            } else {
                user.setActivationCodeSentTimes(0);
            }

        }
    }

    @Transactional
    @Override
    public void activate(ActivationRequest request) {
        User user = userService.getByEmail(request.email());
        validateConfirmEmail(user);

        if (request.code().equals(user.getActivationCode())) {
            user.setActivationCode(null);
            user.setActivationCodeSentTimes(0);
            user.setActivationCodeLastSentAt(null);
            user.setStatus(UserStatus.ACTIVE);
            user.setInvalidActivationCodeEnteredTimes(0);
            user.setInvalidActivationCodeEnteredLastTimeAt(null);
            userService.save(user);
            return;
        } else {
            int confirmationCodeEnteredTimes = user.getInvalidActivationCodeEnteredTimes() + 1;
            user.setInvalidActivationCodeEnteredTimes(confirmationCodeEnteredTimes);
            user.setInvalidActivationCodeEnteredLastTimeAt(LocalDateTime.now());
            userService.save(user);
        }

        throw new ValidationException("You have entered an invalid confirmation code");
    }

    private void validateConfirmEmail(User user) {
        int confirmationCodeEnteredTimes = user.getInvalidActivationCodeEnteredTimes() + 1;

        if (confirmationCodeEnteredTimes >= maxFailedCodeEnteringAttempts) {

            LocalDateTime invalidConfirmationCodeEnteredLastTimeAt = user.getInvalidActivationCodeEnteredLastTimeAt();
            if (invalidConfirmationCodeEnteredLastTimeAt != null &&
                    invalidConfirmationCodeEnteredLastTimeAt.plusMinutes(5L).isAfter(LocalDateTime.now())) {
                throw new ValidationException("You have exceeded confirmation code entering attempts. Please try again after %s"
                        .formatted(invalidConfirmationCodeEnteredLastTimeAt.plusMinutes(5L).format(DateTimeFormatter.ISO_DATE_TIME)));
            } else {
                user.setInvalidActivationCodeEnteredTimes(0);
            }

        }
    }

    @Value("${app.activation.email.maxActivationEmailAttempts:3}")
    public void setMaxActivationEmailAttempts(int maxActivationEmailAttempts) {
        this.maxActivationEmailAttempts = maxActivationEmailAttempts;
    }

    @Value("${app.activation.email.maxFailedCodeEnteringAttempts:3}")
    public void setMaxFailedCodeEnteringAttempts(int maxFailedCodeEnteringAttempts) {
        this.maxFailedCodeEnteringAttempts = maxFailedCodeEnteringAttempts;
    }

}
