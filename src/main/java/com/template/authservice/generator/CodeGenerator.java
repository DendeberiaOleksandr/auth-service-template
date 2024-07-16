package com.template.authservice.generator;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class CodeGenerator {

    private static final Random RANDOM = new Random();

    public String generate() {
        return "" + RANDOM.nextInt(1000, 9999);
    }

}
