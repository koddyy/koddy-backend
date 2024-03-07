package com.koddy.server.global.utils.redis

import java.time.Duration
import java.util.concurrent.TimeUnit

interface RedisOperator<K, V> {
    fun save(
        key: K,
        value: V,
        timeout: Long,
        timeUnit: TimeUnit,
    )

    fun save(
        key: K,
        value: V,
        duration: Duration,
    )

    fun get(key: K): V?

    fun contains(key: K): Boolean

    fun delete(key: K): Boolean

    fun incr(key: K): Long

    fun incr(
        key: K,
        timeout: Long,
        timeUnit: TimeUnit,
    ): Long

    fun incr(
        key: K,
        duration: Duration,
    ): Long
}
