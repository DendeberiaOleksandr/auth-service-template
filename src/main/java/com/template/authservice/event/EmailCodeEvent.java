package com.template.authservice.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EmailCodeEvent extends ApplicationEvent {

    private final String code;
    private final String email;

    public EmailCodeEvent(Object source, String code, String email) {
        super(source);
        this.code = code;
        this.email = email;
    }
}
