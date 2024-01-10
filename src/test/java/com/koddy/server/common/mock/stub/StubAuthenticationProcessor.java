package com.koddy.server.common.mock.stub;

import com.koddy.server.auth.application.adapter.AuthenticationProcessor;
import com.koddy.server.auth.domain.model.code.AuthCodeGenerator;
import com.koddy.server.auth.exception.AuthException;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

import static com.koddy.server.auth.exception.AuthExceptionCode.INVALID_AUTH_CODE;

@Getter
public class StubAuthenticationProcessor implements AuthenticationProcessor {
    private final Map<String, String> cache = new HashMap<>();
    private final AuthCodeGenerator authCodeGenerator;

    public StubAuthenticationProcessor(final AuthCodeGenerator authCodeGenerator) {
        this.authCodeGenerator = authCodeGenerator;
    }

    @Override
    public String storeAuthCode(final String key) {
        return cache.put(key, authCodeGenerator.get());
    }

    @Override
    public void verifyAuthCode(final String key, final String value) {
        final String realValue = cache.get(key);

        if (realValue == null || !realValue.equals(value)) {
            throw new AuthException(INVALID_AUTH_CODE);
        }
    }

    @Override
    public void deleteAuthCode(final String key) {
        cache.remove(key);
    }
}
