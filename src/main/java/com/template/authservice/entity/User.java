package com.template.authservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Table(name = "users")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;

    @Builder.Default
    private boolean enabled = true;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserStatus status = UserStatus.UNVERIFIED;

    @Column(name = "activation_code")
    private String activationCode;

    @Column(name = "activation_code_last_sent_at")
    private LocalDateTime activationCodeLastSentAt;

    @Column(name = "activation_code_sent_times")
    private int activationCodeSentTimes;

    @Column(name = "invalid_activation_code_entered_times")
    private int invalidActivationCodeEnteredTimes;

    @Column(name = "invalid_activation_code_entered_last_time_at")
    private LocalDateTime invalidActivationCodeEnteredLastTimeAt;

    @Column(name = "reset_password_code")
    private String resetPasswordCode;

    @Column(name = "reset_password_code_last_sent_at")
    private LocalDateTime resetPasswordCodeLastSentAt;

    @Column(name = "reset_password_sent_times")
    private int resetPasswordSentTimes;

    @Column(name = "invalid_reset_password_code_entered_times")
    private int invalidResetPasswordCodeEnteredTimes;

    @Column(name = "invalid_reset_password_code_entered_last_time_at")
    private LocalDateTime invalidResetPasswordCodeEnteredLastTimeAt;

}
