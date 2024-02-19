package com.koddy.server.global.utils.redis

import com.koddy.server.global.exception.GlobalException
import com.koddy.server.global.exception.GlobalExceptionCode.UNEXPECTED_SERVER_ERROR
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.concurrent.TimeUnit

@Component
class StringRedisOperator(
    private val redisTemplate: StringRedisTemplate,
) : RedisOperator<String, String?> {
    private val executor: ValueOperations<String, String> = redisTemplate.opsForValue()

    override fun save(
        key: String,
        value: String?,
        timeout: Long,
        timeUnit: TimeUnit,
    ) {
        if (value.isNullOrEmpty()) {
            throw GlobalException(UNEXPECTED_SERVER_ERROR)
        }
        executor.set(key, value, timeout, timeUnit)
    }

    override fun save(
        key: String,
        value: String?,
        duration: Duration,
    ) {
        if (value.isNullOrEmpty()) {
            throw GlobalException(UNEXPECTED_SERVER_ERROR)
        }
        executor.set(key, value, duration)
    }

    override fun get(key: String): String? = executor.get(key)

    override fun contains(key: String): Boolean = !executor.get(key).isNullOrBlank()

    override fun delete(key: String): Boolean = redisTemplate.delete(key)

    override fun incr(key: String): Long = executor.increment(key)!!

    override fun incr(
        key: String,
        timeout: Long,
        timeUnit: TimeUnit,
    ): Long {
        val count: Long = executor.increment(key)!!
        if (count == 1L) {
            executor.getAndExpire(key, timeout, timeUnit)
        }
        return count
    }

    override fun incr(
        key: String,
        duration: Duration,
    ): Long {
        val count: Long = executor.increment(key)!!
        if (count == 1L) {
            executor.getAndExpire(key, duration)
        }
        return count
    }
}
