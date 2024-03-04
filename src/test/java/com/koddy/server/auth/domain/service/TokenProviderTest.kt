package com.koddy.server.auth.domain.service

import com.koddy.server.auth.exception.AuthException
import com.koddy.server.auth.exception.AuthExceptionCode.INVALID_TOKEN
import com.koddy.server.common.UnitTestKt
import com.koddy.server.common.fixture.MentorFixtureStore.mentorFixture
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
    val invalidProvider = TokenProvider(
        secretKey = secretKey,
        accessTokenValidityInSeconds = 0L,
        refreshTokenValidityInSeconds = 0L,
    )
    val validProvider = TokenProvider(
        secretKey = secretKey,
        accessTokenValidityInSeconds = 7200L,
        refreshTokenValidityInSeconds = 7200L,
    )

    val member: Member<*> = mentorFixture(id = 1).toDomain()

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
            val liveToken: String = validProvider.createAccessToken(member.id, member.authority)
            val expiredToken: String = invalidProvider.createAccessToken(member.id, member.authority)

            assertSoftly {
                shouldNotThrowAny { validProvider.validateAccessToken(liveToken) }
                shouldThrow<AuthException> {
                    invalidProvider.validateAccessToken(expiredToken)
                } shouldHaveMessage INVALID_TOKEN.message
            }
        }
    }

    feature("TokenProvider's validateToken (Malformed)") {
        scenario("토큰 조작에 대한 유효성 검사를 진행한다") {
            val validToken: String = validProvider.createAccessToken(member.id, member.authority)
            val forgedToken: String = validProvider.createAccessToken(member.id, member.authority) + "hacked"

            assertSoftly {
                shouldNotThrowAny { validProvider.validateAccessToken(validToken) }
                shouldThrow<AuthException> {
                    validProvider.validateAccessToken(forgedToken)
                } shouldHaveMessage INVALID_TOKEN.message
            }
        }
    }

    feature("TokenProvider's validateToken (Different Subject)") {
        scenario("토큰 Subject에 대한 유효성 검사를 진행한다") {
            val accessToken: String = validProvider.createAccessToken(member.id, member.authority)
            val refreshToken: String = validProvider.createRefreshToken(member.id)

            assertSoftly {
                shouldNotThrowAny { validProvider.validateAccessToken(accessToken) }
                shouldNotThrowAny { validProvider.validateRefreshToken(refreshToken) }
                shouldThrow<AuthException> {
                    validProvider.validateAccessToken(refreshToken)
                } shouldHaveMessage INVALID_TOKEN.message
                shouldThrow<AuthException> {
                    validProvider.validateRefreshToken(accessToken)
                } shouldHaveMessage INVALID_TOKEN.message
            }
        }
    }
})
