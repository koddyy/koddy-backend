package com.koddy.server.common.utils

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class RedisCleaner(
    private val redisTemplate: StringRedisTemplate,
) {
    fun cleanUpRedis() {
        redisTemplate.connectionFactory
            ?.connection
            ?.serverCommands()
            ?.flushAll()
    }
}
