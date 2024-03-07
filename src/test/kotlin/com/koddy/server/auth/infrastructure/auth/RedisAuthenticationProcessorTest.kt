package com.koddy.server.auth.infrastructure.auth

import com.koddy.server.auth.domain.model.code.AuthCodeGenerator
import com.koddy.server.auth.exception.AuthException
import com.koddy.server.auth.exception.AuthExceptionCode.INVALID_AUTH_CODE
import com.koddy.server.common.RedisTestKt
import com.koddy.server.global.utils.redis.RedisOperator
import com.koddy.server.global.utils.redis.StringRedisOperator
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import java.util.UUID

@RedisTestKt
@Import(
    RedisAuthenticationProcessor::class,
    StringRedisOperator::class,
    RedisAuthenticationProcessorTest.FakeConfig::class,
)
@DisplayName("Auth -> RedisAuthenticationProcessor 테스트")
internal class RedisAuthenticationProcessorTest(
    private val sut: RedisAuthenticationProcessor,
    private val redisOperator: RedisOperator<String, String>,
) {
    @TestConfiguration
    internal class FakeConfig {
        @Bean
        fun authCodeGenerator(): AuthCodeGenerator = AuthCodeGenerator { AUTH_CODE }
    }

    @Test
    fun `Redis에 인증번호를 저장한다`() {
        // when
        val result: String = sut.storeAuthCode(AUTH_KEY)

        // then
        assertSoftly {
            result shouldBe AUTH_CODE
            redisOperator.get(AUTH_KEY) shouldBe AUTH_CODE
        }
    }

    @Test
    fun `Redis에 저장된 인증번호와 요청 인증번호가 일치하는지 확인한다`() {
        // given
        sut.storeAuthCode(AUTH_KEY)

        // when - then
        assertSoftly {
            shouldNotThrowAny {
                sut.verifyAuthCode(AUTH_KEY, AUTH_CODE)
            }
            shouldThrow<AuthException> {
                sut.verifyAuthCode(AUTH_KEY, "${AUTH_CODE}_wrong")
            } shouldHaveMessage INVALID_AUTH_CODE.message
        }
    }

    @Test
    fun `인증 완료 후 Redis에 저장된 인증번호를 제거한다`() {
        // given
        sut.storeAuthCode(AUTH_KEY)
        redisOperator.get(AUTH_KEY) shouldBe AUTH_CODE

        // when
        sut.deleteAuthCode(AUTH_KEY)

        // then
        redisOperator.get(AUTH_KEY) shouldBe null
    }

    companion object {
        private const val AUTH_KEY: String = "MAIL-AUTH:sjiwon4491@gmail.com"
        private val AUTH_CODE: String =
            UUID.randomUUID()
                .toString()
                .replace("-".toRegex(), "")
                .substring(0, 8)
    }
}
