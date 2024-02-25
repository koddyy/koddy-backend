package com.koddy.server.auth.domain.model.oauth

import com.koddy.server.auth.exception.AuthException
import com.koddy.server.auth.exception.AuthExceptionCode
import com.koddy.server.common.UnitTestKt
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage

@UnitTestKt
@DisplayName("Auth -> 도메인 [OAuthProvider] 테스트")
internal class OAuthProviderTest : DescribeSpec({
    describe("OAuthProvider's from") {
        context("제공하지 않는 Provider는") {
            it("INVALID_OAUTH_PROVIDER 예외가 발생한다") {
                shouldThrow<AuthException> {
                    OAuthProvider.from("anonymous")
                } shouldHaveMessage AuthExceptionCode.INVALID_OAUTH_PROVIDER.message
            }
        }

        context("제공하는 Provider는") {
            it("OAuthProvider로 변환해서 응답한다") {
                assertSoftly {
                    OAuthProvider.from("google") shouldBe OAuthProvider.GOOGLE
                    OAuthProvider.from("kakao") shouldBe OAuthProvider.KAKAO
                    OAuthProvider.from("zoom") shouldBe OAuthProvider.ZOOM
                }
            }
        }
    }
})
