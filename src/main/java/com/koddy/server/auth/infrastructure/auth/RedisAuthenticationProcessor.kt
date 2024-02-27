package com.koddy.server.auth.infrastructure.auth

import com.koddy.server.auth.application.adapter.AuthenticationProcessor
import com.koddy.server.auth.domain.model.code.AuthCodeGenerator
import com.koddy.server.auth.exception.AuthException
import com.koddy.server.auth.exception.AuthExceptionCode.INVALID_AUTH_CODE
import com.koddy.server.global.utils.redis.RedisOperator
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class RedisAuthenticationProcessor(
    private val authCodeGenerator: AuthCodeGenerator,
    private val redisOperator: RedisOperator<String, String>,
    @Value("\${mail.auth.ttl}") private val authTtl: Long,
) : AuthenticationProcessor {
    override fun storeAuthCode(key: String): String {
        val authCode: String = authCodeGenerator.get()
        redisOperator.save(key, authCode, Duration.ofSeconds(authTtl))
        return authCode
    }

    override fun verifyAuthCode(key: String, value: String) {
        val realValue: String? = redisOperator.get(key)

        if (realValue.isNullOrBlank() || realValue != value) {
            throw AuthException(INVALID_AUTH_CODE)
        }
    }

    override fun deleteAuthCode(key: String) {
        redisOperator.delete(key)
    }
}
