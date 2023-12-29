package com.koddy.server.auth.infrastructure;

import com.koddy.server.auth.application.adapter.MailAuthenticationProcessor;
import com.koddy.server.auth.domain.model.code.AuthCodeGenerator;
import com.koddy.server.auth.exception.AuthException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.koddy.server.auth.exception.AuthExceptionCode.INVALID_AUTH_CODE;

@Component
public class RedisMailAuthenticationProcessor implements MailAuthenticationProcessor {
    private final AuthCodeGenerator authCodeGenerator;
    private final StringRedisTemplate stringRedisTemplate;
    private final long authTtl;

    public RedisMailAuthenticationProcessor(
            final AuthCodeGenerator authCodeGenerator,
            final StringRedisTemplate stringRedisTemplate,
            @Value("${mail.auth.ttl}") final long authTtl
    ) {
        this.authCodeGenerator = authCodeGenerator;
        this.stringRedisTemplate = stringRedisTemplate;
        this.authTtl = authTtl;
    }

    @Override
    public String storeAuthCode(final String key) {
        final String authCode = authCodeGenerator.get();
        stringRedisTemplate.opsForValue().set(key, authCode, authTtl, TimeUnit.SECONDS);
        return authCode;
    }

    @Override
    public void verifyAuthCode(final String key, final String value) {
        final String realValue = stringRedisTemplate.opsForValue().get(key);

        if (realValue == null || !realValue.equals(value)) {
            throw new AuthException(INVALID_AUTH_CODE);
        }
    }

    @Override
    public void deleteAuthCode(final String key) {
        stringRedisTemplate.delete(key);
    }
}
