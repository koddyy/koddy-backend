package com.koddy.server.auth.infrastructure.auth;

import com.koddy.server.auth.application.adapter.AuthenticationProcessor;
import com.koddy.server.auth.domain.model.code.AuthCodeGenerator;
import com.koddy.server.auth.exception.AuthException;
import com.koddy.server.global.redis.RedisOperator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;

import static com.koddy.server.auth.exception.AuthExceptionCode.INVALID_AUTH_CODE;

@Component
public class RedisAuthenticationProcessor implements AuthenticationProcessor {
    private final AuthCodeGenerator authCodeGenerator;
    private final RedisOperator<String, String> redisOperator;
    private final long authTtl;

    public RedisAuthenticationProcessor(
            final AuthCodeGenerator authCodeGenerator,
            final RedisOperator<String, String> redisOperator,
            @Value("${mail.auth.ttl}") final long authTtl
    ) {
        this.authCodeGenerator = authCodeGenerator;
        this.redisOperator = redisOperator;
        this.authTtl = authTtl;
    }

    @Override
    public String storeAuthCode(final String key) {
        final String authCode = authCodeGenerator.get();
        redisOperator.save(key, authCode, Duration.ofSeconds(authTtl));
        return authCode;
    }

    @Override
    public void verifyAuthCode(final String key, final String value) {
        final String realValue = redisOperator.get(key);

        if (realValue == null || !realValue.equals(value)) {
            throw new AuthException(INVALID_AUTH_CODE);
        }
    }

    @Override
    public void deleteAuthCode(final String key) {
        redisOperator.delete(key);
    }
}
