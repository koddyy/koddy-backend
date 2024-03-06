package com.koddy.server.auth.infrastructure.token

import com.koddy.server.common.RedisTestKt
import com.koddy.server.common.utils.TokenDummy.REFRESH_TOKEN
import com.koddy.server.global.utils.redis.RedisOperator
import com.koddy.server.global.utils.redis.StringRedisOperator
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.context.annotation.Import
import java.time.Duration

@RedisTestKt
@Import(
    RedisTokenStore::class,
    StringRedisOperator::class,
)
@DisplayName("Auth -> RedisTokenStore 테스트")
internal class RedisTokenStoreTest(
    private val sut: RedisTokenStore,
    private val redisOperator: RedisOperator<String, String>,
) {
    @Nested
    @DisplayName("RefreshToken 동기화")
    internal inner class SynchronizeRefreshToken {
        @Test
        fun `RefreshToken을 보유하고 있지 않은 사용자에게는 새로운 RefreshToken을 발급한다`() {
            // when
            sut.synchronizeRefreshToken(MEMBER_ID, REFRESH_TOKEN)

            // then
            val token: String? = redisOperator.get(createKey())
            assertSoftly {
                token shouldNotBe null
                token shouldBe REFRESH_TOKEN
            }
        }

        @Test
        fun `RefreshToken을 보유하고 있는 사용자에게는 새로운 RefreshToken으로 업데이트한다`() {
            // given
            redisOperator.save(createKey(), REFRESH_TOKEN, Duration.ofSeconds(1234))

            // when
            val newRefreshToken = "${REFRESH_TOKEN}_new"
            sut.synchronizeRefreshToken(MEMBER_ID, newRefreshToken)

            // then
            val token: String? = redisOperator.get(createKey())
            assertSoftly {
                token shouldNotBe null
                token shouldNotBe REFRESH_TOKEN
                token shouldBe newRefreshToken
            }
        }
    }

    @Test
    fun `사용자가 보유하고 있는 RefreshToken을 재발급한다`() {
        // given
        redisOperator.save(createKey(), REFRESH_TOKEN, Duration.ofSeconds(1234))

        // when
        val newRefreshToken = "${REFRESH_TOKEN}_new"
        sut.updateRefreshToken(MEMBER_ID, newRefreshToken)

        // then
        val token: String? = redisOperator.get(createKey())
        assertSoftly {
            token shouldNotBe null
            token shouldNotBe REFRESH_TOKEN
            token shouldBe newRefreshToken
        }
    }

    @Test
    fun `사용자가 보유하고 있는 RefreshToken을 삭제한다`() {
        // given
        redisOperator.save(createKey(), REFRESH_TOKEN, Duration.ofSeconds(1234))

        // when
        sut.deleteRefreshToken(MEMBER_ID)

        // then
        redisOperator.get(createKey()) shouldBe null
    }

    @Test
    fun `사용자 소유의 RefreshToken인지 확인한다`() {
        // given
        redisOperator.save(createKey(), REFRESH_TOKEN, Duration.ofSeconds(1234))

        // when
        val actual1: Boolean = sut.isMemberRefreshToken(MEMBER_ID, REFRESH_TOKEN)
        val actual2: Boolean = sut.isMemberRefreshToken(MEMBER_ID, "${REFRESH_TOKEN}_fake")

        // then
        assertSoftly {
            actual1 shouldBe true
            actual2 shouldBe false
        }
    }

    private fun createKey(): String = String.format(KEY, MEMBER_ID)

    companion object {
        private const val KEY: String = "TOKEN:%s"
        private const val MEMBER_ID: Long = 1L
    }
}
