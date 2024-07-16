package com.template.authservice.event;

import lombok.Getter;

@Getter
public class EmailActivationEvent extends EmailCodeEvent {
    public EmailActivationEvent(Object source, String code, String email) {
        super(source, code, email);
    }
}
