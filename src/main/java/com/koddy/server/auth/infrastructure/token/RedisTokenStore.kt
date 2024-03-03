package com.koddy.server.auth.infrastructure.token

import com.koddy.server.auth.application.adapter.TokenStore
import com.koddy.server.global.utils.redis.RedisOperator
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class RedisTokenStore(
    @Value("\${jwt.refresh-token-validity-seconds}") private val refreshTokenValidityInSeconds: Long,
    private val redisOperator: RedisOperator<String, String>,
) : TokenStore {
    override fun synchronizeRefreshToken(memberId: Long, refreshToken: String) =
        redisOperator.save(
            key = createKey(memberId),
            value = refreshToken,
            duration = Duration.ofSeconds(refreshTokenValidityInSeconds),
        )

    override fun updateRefreshToken(memberId: Long, newRefreshToken: String) =
        redisOperator.save(
            key = createKey(memberId),
            value = newRefreshToken,
            duration = Duration.ofSeconds(refreshTokenValidityInSeconds),
        )

    override fun deleteRefreshToken(memberId: Long) {
        redisOperator.delete(key = createKey(memberId))
    }

    override fun isMemberRefreshToken(memberId: Long, refreshToken: String): Boolean {
        val validToken: String? = redisOperator.get(key = createKey(memberId))
        return validToken.isNullOrBlank().not() && refreshToken == validToken
    }

    private fun createKey(memberId: Long): String = String.format("TOKEN:%s", memberId)
}
