package com.template.authservice.event;

public class EmailResetPasswordEvent extends EmailCodeEvent {
    public EmailResetPasswordEvent(Object source, String code, String email) {
        super(source, code, email);
    }
}
