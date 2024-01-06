package com.koddy.server.global.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class DefaultRedisStringOperator {
    private final StringRedisTemplate redisTemplate;

    public void save(final String key, final String value, final Duration duration) {
        redisTemplate.opsForValue().set(key, value, duration);
    }

    public String get(final String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public boolean contains(final String key) {
        return redisTemplate.opsForValue().get(key) != null;
    }

    public void delete(final String key) {
        redisTemplate.delete(key);
    }
}
