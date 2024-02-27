package com.koddy.server.auth.domain.service

import com.koddy.server.auth.exception.AuthException
import com.koddy.server.auth.exception.AuthExceptionCode.INVALID_TOKEN
import com.koddy.server.common.UnitTestKt
import com.koddy.server.common.fixture.MentorFixture
import com.koddy.server.member.domain.model.Member
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.throwable.shouldHaveMessage

@UnitTestKt
@DisplayName("Auth -> TokenProvider 테스트")
internal class TokenProviderTest : FeatureSpec({
    val secretKey = "asldfjsadlfjalksjf01jf02j9012f0120f12jf1j29v0saduf012ue101212c01"
    val invalidProvider = TokenProvider(secretKey, 0L, 0L)
    val validProvider = TokenProvider(secretKey, 7200L, 7200L)

    val member: Member<*> = MentorFixture.MENTOR_1.toDomain().apply(1L)

    feature("TokenProvider's createAccessToken & createRefreshToken") {
        scenario("AccessToken과 RefreshToken을 발급한다") {
            val accessToken: String = validProvider.createAccessToken(member.id, member.authority)
            val refreshToken: String = validProvider.createRefreshToken(member.id)

            assertSoftly {
                accessToken shouldNotBe null
                refreshToken shouldNotBe null
            }
        }
    }

    feature("TokenProvider's getId & getAuthority") {
        scenario("토큰의 Payload(ID, Authority)를 추출한다") {
            val accessToken: String = validProvider.createAccessToken(member.id, member.authority)
            val refreshToken: String = validProvider.createRefreshToken(member.id)

            assertSoftly {
                validProvider.getId(accessToken) shouldBe member.id
                validProvider.getAuthority(accessToken) shouldBe member.authority
                validProvider.getId(refreshToken) shouldBe member.id
            }
        }
    }

    feature("TokenProvider's validateToken (Expired)") {
        scenario("토큰 만료에 대한 유효성 검사를 진행한다") {
            val validToken: String = validProvider.createAccessToken(member.id, member.authority)
            val invalidToken: String = invalidProvider.createAccessToken(member.id, member.authority)

            assertSoftly {
                shouldNotThrowAny { validProvider.validateToken(validToken) }
                shouldThrow<AuthException> {
                    invalidProvider.validateToken(invalidToken)
                } shouldHaveMessage INVALID_TOKEN.message
            }
        }
    }

    feature("TokenProvider's validateToken (Malformed)") {
        scenario("토큰 조작에 대한 유효성 검사를 진행한다") {
            val validToken: String = validProvider.createAccessToken(member.id, member.authority)
            val forgedToken: String = validProvider.createAccessToken(member.id, member.authority) + "hacked"

            assertSoftly {
                shouldNotThrowAny { validProvider.validateToken(validToken) }
                shouldThrow<AuthException> {
                    validProvider.validateToken(forgedToken)
                } shouldHaveMessage INVALID_TOKEN.message
            }
        }
    }
})
