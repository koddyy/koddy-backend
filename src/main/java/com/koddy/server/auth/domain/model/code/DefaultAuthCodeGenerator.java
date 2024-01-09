package com.koddy.server.auth.domain.model.code;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DefaultAuthCodeGenerator implements AuthCodeGenerator {
    @Override
    public String get() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
    }
}
