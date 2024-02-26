package com.koddy.server.auth.utils

import com.koddy.server.auth.domain.model.AuthToken
import com.koddy.server.common.UnitTestKt
import com.koddy.server.common.utils.TokenUtils.ACCESS_TOKEN
import com.koddy.server.common.utils.TokenUtils.REFRESH_TOKEN
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest

@UnitTestKt
@DisplayName("Auth -> TokenExtractor 테스트")
internal class TokenExtractorTest : DescribeSpec({
    val request = mockk<HttpServletRequest>()

    describe("TokenExtractor's extractAccessToken") {
        context("Authorization Header에 Bearer 타입 & AccessToken 둘다 없으면") {
            every { request.getHeader(AuthToken.ACCESS_TOKEN_HEADER) } returns null

            it("null을 응답한다") {
                val token: String? = TokenExtractor.extractAccessToken(request)
                token shouldBe null
            }
        }

        context("Authorization Header에 Bearer 타입만 있으면") {
            every { request.getHeader(AuthToken.ACCESS_TOKEN_HEADER) } returns AuthToken.TOKEN_TYPE

            it("null을 응답한다") {
                val token: String? = TokenExtractor.extractAccessToken(request)
                token shouldBe null
            }
        }

        context("Authorization Header에 Bearer 타입 & AccessToken 둘다 있으면") {
            every { request.getHeader(AuthToken.ACCESS_TOKEN_HEADER) } returns "${AuthToken.TOKEN_TYPE} $ACCESS_TOKEN"

            it("정상적으로 AccessToken을 추출한다") {
                val token: String? = TokenExtractor.extractAccessToken(request)
                token shouldBe ACCESS_TOKEN
            }
        }
    }

    describe("TokenExtractor's extractRefreshToken") {
        context("Cookie Header에 RefreshToken이 없으면") {
            every { request.cookies } returns emptyArray()

            it("null을 응답한다") {
                val token: String? = TokenExtractor.extractRefreshToken(request)
                token shouldBe null
            }
        }

        context("Cookie Header에 RefreshToken이 있으면") {
            every { request.cookies } returns arrayOf(Cookie(AuthToken.REFRESH_TOKEN_HEADER, REFRESH_TOKEN))

            it("정상적으로 RefreshToken을 추출한다") {
                val token: String? = TokenExtractor.extractRefreshToken(request)
                token shouldBe REFRESH_TOKEN
            }
        }
    }
})
