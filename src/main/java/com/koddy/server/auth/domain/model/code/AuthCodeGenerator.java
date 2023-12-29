package com.koddy.server.auth.domain.model.code;

@FunctionalInterface
public interface AuthCodeGenerator {
    String get();
}
