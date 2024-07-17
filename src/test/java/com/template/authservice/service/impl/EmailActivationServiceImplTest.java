package com.template.authservice.service.impl;

import com.template.authservice.dto.activation.ActivationRequest;
import com.template.authservice.dto.activation.SendActivationEmailRequest;
import com.template.authservice.entity.User;
import com.template.authservice.entity.UserStatus;
import com.template.authservice.generator.CodeGenerator;
import com.template.authservice.service.UserService;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailActivationServiceImplTest {

    private static final String EMAIL = "email";
    private static final String CODE = "4581";
    private static final int MAX_ATTEMPTS = 3;

    @InjectMocks
    EmailActivationServiceImpl emailActivationService;

    @Mock
    CodeGenerator codeGenerator;
    @Mock
    UserService userService;
    @Mock
    ApplicationEventPublisher eventPublisher;

    @Test
    void sendEmail_whenUserExceededSendEmailAttemptsAndTimeIsNotGone() {
        // given
        emailActivationService.setMaxActivationEmailAttempts(MAX_ATTEMPTS);

        User user = new User();
        user.setActivationCodeSentTimes(MAX_ATTEMPTS + 1);
        user.setActivationCodeLastSentAt(LocalDateTime.now().minusMinutes(1L));

        when(userService.getByEmail(eq(EMAIL))).thenReturn(user);

        // when
        assertThrows(ValidationException.class, () -> emailActivationService.sendEmail(new SendActivationEmailRequest(EMAIL)));

        // then
    }

    @Test
    void sendEmail_whenUserExceededSendEmailAttemptsAndTimeIsGone() {
        // given
        emailActivationService.setMaxActivationEmailAttempts(MAX_ATTEMPTS);

        User user = new User();
        user.setActivationCodeSentTimes(MAX_ATTEMPTS + 1);
        user.setActivationCodeLastSentAt(LocalDateTime.now().minusMinutes(30L));

        when(userService.getByEmail(eq(EMAIL))).thenReturn(user);

        // when
        emailActivationService.sendEmail(new SendActivationEmailRequest(EMAIL));

        // then
        verify(userService, times(1)).save(any());
        verify(eventPublisher, times(1)).publishEvent(any());
        assertEquals(1, user.getActivationCodeSentTimes());
    }

    @Test
    void sendEmail_whenUserDoesNotExceededSendEmailAttempts() {
        // given
        emailActivationService.setMaxActivationEmailAttempts(MAX_ATTEMPTS);

        User user = new User();
        final int activationCodeSentTimes = MAX_ATTEMPTS - 1;
        user.setActivationCodeSentTimes(activationCodeSentTimes);
        user.setActivationCodeLastSentAt(LocalDateTime.now().minusMinutes(1L));

        when(userService.getByEmail(eq(EMAIL))).thenReturn(user);

        // when
        emailActivationService.sendEmail(new SendActivationEmailRequest(EMAIL));

        // then
        verify(userService, times(1)).save(any());
        verify(eventPublisher, times(1)).publishEvent(any());
        assertEquals(activationCodeSentTimes + 1, user.getActivationCodeSentTimes());
    }

    @Test
    void activate_whenUserEntersInvalidCodeAndDoesNotExceedMaxAttempts() {
        // given
        User user = new User();
        final int codeEnteredTimes = 0;
        user.setInvalidActivationCodeEnteredTimes(codeEnteredTimes);

        emailActivationService.setMaxFailedCodeEnteringAttempts(MAX_ATTEMPTS);

        when(userService.getByEmail(eq(EMAIL))).thenReturn(user);

        // when
        assertThrows(ValidationException.class, () -> emailActivationService.activate(new ActivationRequest(EMAIL, CODE)));

        // then
        assertEquals(codeEnteredTimes + 1, user.getInvalidActivationCodeEnteredTimes());
    }

    @Test
    void activate_whenUserEntersInvalidCodeAndExceedsMaxAttemptsAndTimeIsNotGone() {
        // given
        User user = new User();
        final int codeEnteredTimes = MAX_ATTEMPTS + 1;
        user.setInvalidActivationCodeEnteredTimes(codeEnteredTimes);
        user.setInvalidActivationCodeEnteredLastTimeAt(LocalDateTime.now().minusMinutes(1L));

        emailActivationService.setMaxFailedCodeEnteringAttempts(MAX_ATTEMPTS);

        when(userService.getByEmail(eq(EMAIL))).thenReturn(user);

        // when
        assertThrows(ValidationException.class, () -> emailActivationService.activate(new ActivationRequest(EMAIL, CODE)));

        // then
        assertEquals(codeEnteredTimes, user.getInvalidActivationCodeEnteredTimes());
    }

    @Test
    void activate_whenUserEntersInvalidCodeAndExceedsMaxAttemptsAndTimeIsGone() {
        // given
        User user = new User();
        final int codeEnteredTimes = MAX_ATTEMPTS + 1;
        user.setInvalidActivationCodeEnteredTimes(codeEnteredTimes);
        user.setInvalidActivationCodeEnteredLastTimeAt(LocalDateTime.now().minusMinutes(30L));

        emailActivationService.setMaxFailedCodeEnteringAttempts(MAX_ATTEMPTS);

        when(userService.getByEmail(eq(EMAIL))).thenReturn(user);

        // when
        assertThrows(ValidationException.class, () -> emailActivationService.activate(new ActivationRequest(EMAIL, CODE)));

        // then
        assertEquals(1, user.getInvalidActivationCodeEnteredTimes());
    }

    @Test
    void activate_whenUserEntersValidCode() {
        // given
        User user = new User();
        user.setInvalidActivationCodeEnteredTimes(0);
        user.setActivationCode(CODE);

        emailActivationService.setMaxFailedCodeEnteringAttempts(MAX_ATTEMPTS);

        when(userService.getByEmail(eq(EMAIL))).thenReturn(user);

        // when
        emailActivationService.activate(new ActivationRequest(EMAIL, CODE));

        // then
        assertEquals(0, user.getInvalidActivationCodeEnteredTimes());
        assertNull(user.getInvalidActivationCodeEnteredLastTimeAt());
        assertNull(user.getActivationCode());
        assertEquals(0, user.getActivationCodeSentTimes());
        assertNull(user.getActivationCodeLastSentAt());
        assertEquals(UserStatus.ACTIVE, user.getStatus());
    }

}