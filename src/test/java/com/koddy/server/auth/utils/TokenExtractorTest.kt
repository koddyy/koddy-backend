package com.koddy.server.auth.utils

import com.koddy.server.auth.domain.model.AuthToken.ACCESS_TOKEN_HEADER
import com.koddy.server.auth.domain.model.AuthToken.REFRESH_TOKEN_HEADER
import com.koddy.server.auth.domain.model.AuthToken.TOKEN_TYPE
import com.koddy.server.common.UnitTestKt
import com.koddy.server.common.utils.TokenUtils.ACCESS_TOKEN
import com.koddy.server.common.utils.TokenUtils.REFRESH_TOKEN
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest

@UnitTestKt
@DisplayName("Auth -> TokenExtractor 테스트")
internal class TokenExtractorTest : BehaviorSpec({
    val request: HttpServletRequest = mockk<HttpServletRequest>()

    Given("HTTP 요청 메시지의 Authorization Header로부터 AccessToken을 추출하려고 할 때") {
        When("Bearer 타입 & AccessToken 둘다 없으면") {
            every { request.getHeader(ACCESS_TOKEN_HEADER) } returns null

            Then("null을 응답한다") {
                val token: String? = TokenExtractor.extractAccessToken(request)
                token.shouldBeNull()
            }
        }

        When("Bearer 타입만 있으면") {
            every { request.getHeader(ACCESS_TOKEN_HEADER) } returns TOKEN_TYPE

            Then("null을 응답한다") {
                val token: String? = TokenExtractor.extractAccessToken(request)
                token.shouldBeNull()
            }
        }

        When("Bearer 타입 & AccessToken 둘다 있으면") {
            every { request.getHeader(ACCESS_TOKEN_HEADER) } returns "$TOKEN_TYPE $ACCESS_TOKEN"

            Then("값을 추출한다") {
                val token: String? = TokenExtractor.extractAccessToken(request)
                token shouldBe ACCESS_TOKEN
            }
        }
    }

    Given("HTTP 요청 메시지의 Cookie Header로부터 RefreshToken을 추출하려고 할 때") {
        When("RefreshToken이 없으면") {
            every { request.cookies } returns emptyArray()

            Then("null을 응답한다") {
                val token: String? = TokenExtractor.extractRefreshToken(request)
                token.shouldBeNull()
            }
        }

        When("RefreshToken이 존재하면") {
            every { request.cookies } returns arrayOf(Cookie(REFRESH_TOKEN_HEADER, REFRESH_TOKEN))

            Then("값을 추출한다") {
                val token: String? = TokenExtractor.extractRefreshToken(request)
                token shouldBe REFRESH_TOKEN
            }
        }
    }
})
