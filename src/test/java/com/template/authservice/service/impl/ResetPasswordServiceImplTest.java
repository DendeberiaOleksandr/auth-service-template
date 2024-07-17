package com.template.authservice.service.impl;

import com.template.authservice.dto.reset.password.ChangePasswordRequest;
import com.template.authservice.dto.reset.password.ResetPasswordRequest;
import com.template.authservice.entity.User;
import com.template.authservice.generator.CodeGenerator;
import com.template.authservice.service.UserService;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

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
class ResetPasswordServiceImplTest {

    private static final String EMAIL = "email";
    private static final String CODE = "4581";
    private static final String PASSWORD = "password";
    private static final int MAX_ATTEMPTS = 3;

    @InjectMocks
    ResetPasswordServiceImpl resetPasswordService;

    @Mock
    CodeGenerator codeGenerator;
    @Mock
    UserService userService;
    @Mock
    ApplicationEventPublisher eventPublisher;
    @Mock
    PasswordEncoder passwordEncoder;

    @Test
    void resetPassword_whenUserExceededSendEmailAttemptsAndTimeIsNotGone() {
        // given
        resetPasswordService.setMaxResetPasswordAttempts(MAX_ATTEMPTS);

        User user = new User();
        user.setResetPasswordSentTimes(MAX_ATTEMPTS + 1);
        user.setResetPasswordCodeLastSentAt(LocalDateTime.now().minusMinutes(1L));

        when(userService.getByEmail(eq(EMAIL))).thenReturn(user);

        // when
        assertThrows(ValidationException.class, () -> resetPasswordService.resetPassword(new ResetPasswordRequest(EMAIL)));

        // then
    }

    @Test
    void resetPassword_whenUserExceededSendEmailAttemptsAndTimeIsGone() {
        // given
        resetPasswordService.setMaxResetPasswordAttempts(MAX_ATTEMPTS);

        User user = new User();
        user.setActivationCodeSentTimes(MAX_ATTEMPTS + 1);
        user.setActivationCodeLastSentAt(LocalDateTime.now().minusMinutes(30L));

        when(userService.getByEmail(eq(EMAIL))).thenReturn(user);

        // when
        resetPasswordService.resetPassword(new ResetPasswordRequest(EMAIL));

        // then
        verify(userService, times(1)).save(any());
        verify(eventPublisher, times(1)).publishEvent(any());
        assertEquals(1, user.getResetPasswordSentTimes());
    }

    @Test
    void resetPassword_whenUserDoesNotExceededSendEmailAttempts() {
        // given
        resetPasswordService.setMaxResetPasswordAttempts(MAX_ATTEMPTS);

        User user = new User();
        final int resetPasswordCodeSentTimes = MAX_ATTEMPTS - 1;
        user.setResetPasswordSentTimes(resetPasswordCodeSentTimes);
        user.setResetPasswordCodeLastSentAt(LocalDateTime.now().minusMinutes(1L));

        when(userService.getByEmail(eq(EMAIL))).thenReturn(user);

        // when
        resetPasswordService.resetPassword(new ResetPasswordRequest(EMAIL));

        // then
        verify(userService, times(1)).save(any());
        verify(eventPublisher, times(1)).publishEvent(any());
        assertEquals(resetPasswordCodeSentTimes + 1, user.getResetPasswordSentTimes());
    }

    @Test
    void changePassword_whenUserEntersInvalidCodeAndDoesNotExceedMaxAttempts() {
        // given
        User user = new User();
        final int codeEnteredTimes = 0;
        user.setInvalidResetPasswordCodeEnteredTimes(codeEnteredTimes);

        resetPasswordService.setMaxFailedCodeEnteringAttempts(MAX_ATTEMPTS);

        when(userService.getByEmail(eq(EMAIL))).thenReturn(user);

        // when
        assertThrows(ValidationException.class, () -> resetPasswordService.changePassword(new ChangePasswordRequest(EMAIL, CODE, PASSWORD)));

        // then
        assertEquals(codeEnteredTimes + 1, user.getInvalidResetPasswordCodeEnteredTimes());
    }

    @Test
    void changePassword_whenUserEntersInvalidCodeAndExceedsMaxAttemptsAndTimeIsNotGone() {
        // given
        User user = new User();
        final int codeEnteredTimes = MAX_ATTEMPTS + 1;
        user.setInvalidResetPasswordCodeEnteredTimes(codeEnteredTimes);
        user.setInvalidResetPasswordCodeEnteredLastTimeAt(LocalDateTime.now().minusMinutes(1L));

        resetPasswordService.setMaxFailedCodeEnteringAttempts(MAX_ATTEMPTS);

        when(userService.getByEmail(eq(EMAIL))).thenReturn(user);

        // when
        assertThrows(ValidationException.class, () -> resetPasswordService.changePassword(new ChangePasswordRequest(EMAIL, CODE, PASSWORD)));

        // then
        assertEquals(codeEnteredTimes, user.getInvalidResetPasswordCodeEnteredTimes());
    }

    @Test
    void changePassword_whenUserEntersInvalidCodeAndExceedsMaxAttemptsAndTimeIsGone() {
        // given
        User user = new User();
        final int codeEnteredTimes = MAX_ATTEMPTS + 1;
        user.setInvalidResetPasswordCodeEnteredTimes(codeEnteredTimes);
        user.setInvalidResetPasswordCodeEnteredLastTimeAt(LocalDateTime.now().minusMinutes(30L));

        resetPasswordService.setMaxFailedCodeEnteringAttempts(MAX_ATTEMPTS);

        when(userService.getByEmail(eq(EMAIL))).thenReturn(user);

        // when
        assertThrows(ValidationException.class, () -> resetPasswordService.changePassword(new ChangePasswordRequest(EMAIL, CODE, PASSWORD)));

        // then
        assertEquals(1, user.getInvalidResetPasswordCodeEnteredTimes());
    }

    @Test
    void changePassword_whenUserEntersValidCode() {
        // given
        User user = new User();
        user.setInvalidResetPasswordCodeEnteredTimes(0);
        user.setResetPasswordCode(CODE);

        resetPasswordService.setMaxFailedCodeEnteringAttempts(MAX_ATTEMPTS);

        when(userService.getByEmail(eq(EMAIL))).thenReturn(user);

        // when
        resetPasswordService.changePassword(new ChangePasswordRequest(EMAIL, CODE, PASSWORD));

        // then
        verify(passwordEncoder, times(1)).encode(eq(PASSWORD));
        assertEquals(0, user.getInvalidResetPasswordCodeEnteredTimes());
        assertNull(user.getInvalidResetPasswordCodeEnteredLastTimeAt());
        assertNull(user.getResetPasswordCode());
        assertEquals(0, user.getResetPasswordSentTimes());
        assertNull(user.getResetPasswordCodeLastSentAt());
    }

}
