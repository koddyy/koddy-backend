package com.koddy.server.auth.domain.model.code;

@FunctionalInterface
public interface AuthKeyGenerator {
    String get(final String prefix, final Object... suffix);
}
