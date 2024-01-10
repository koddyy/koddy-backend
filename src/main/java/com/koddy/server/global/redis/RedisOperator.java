package com.koddy.server.global.redis;

import java.time.Duration;

public interface RedisOperator<K, V> {
    void save(final K key, final V value, final Duration duration);

    V get(final K key);

    boolean contains(final K key);

    void delete(final K key);
}
