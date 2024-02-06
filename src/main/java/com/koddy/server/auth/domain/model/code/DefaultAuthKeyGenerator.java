package com.koddy.server.auth.domain.model.code;

import org.springframework.stereotype.Component;

@Component
public class DefaultAuthKeyGenerator implements AuthKeyGenerator {
    @Override
    public String get(final String prefix, final Object... suffix) {
        return String.format(prefix, suffix);
    }
}
