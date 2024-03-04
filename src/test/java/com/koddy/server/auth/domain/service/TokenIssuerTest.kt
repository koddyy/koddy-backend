package com.koddy.server.auth.domain.service

import com.koddy.server.auth.application.adapter.TokenStore
import com.koddy.server.auth.domain.model.AuthToken
import com.koddy.server.common.UnitTestKt
import com.koddy.server.common.fixture.MentorFixtureStore.mentorFixture
import com.koddy.server.common.utils.TokenUtils.ACCESS_TOKEN
import com.koddy.server.common.utils.TokenUtils.REFRESH_TOKEN
import com.koddy.server.member.domain.model.Member
import io.kotest.assertions.assertSoftly
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify

@UnitTestKt
@DisplayName("Auth -> TokenIssuer 테스트")
internal class TokenIssuerTest : DescribeSpec({
    val tokenProvider = mockk<TokenProvider>()
    val tokenStore = mockk<TokenStore>()
    val sut = TokenIssuer(tokenProvider, tokenStore)

    val member: Member<*> = mentorFixture(id = 1).toDomain()

    describe("TokenIssuer's provideAuthorityToken") {
        context("토큰 발급을 요청하면") {
            every { tokenProvider.createAccessToken(member.id, member.authority) } returns ACCESS_TOKEN
            every { tokenProvider.createRefreshToken(member.id) } returns REFRESH_TOKEN
            justRun { tokenStore.synchronizeRefreshToken(member.id, REFRESH_TOKEN) }

            it("AuthToken[Access + Refresh]을 제공한다") {
                val result: AuthToken = sut.provideAuthorityToken(member.id, member.authority)

                verify(exactly = 1) {
                    tokenProvider.createAccessToken(member.id, member.authority)
                    tokenProvider.createRefreshToken(member.id)
                }
                assertSoftly(result) {
                    accessToken shouldBe ACCESS_TOKEN
                    refreshToken shouldBe REFRESH_TOKEN
                }
            }
        }
    }

    describe("TokenIssuer's reissueAuthorityToken") {
        context("본인 소유의 RefreshToken을 통해서 재발급을 요청하면") {
            every { tokenProvider.createAccessToken(member.id, member.authority) } returns ACCESS_TOKEN
            every { tokenProvider.createRefreshToken(member.id) } returns REFRESH_TOKEN
            justRun { tokenStore.updateRefreshToken(member.id, REFRESH_TOKEN) }

            it("AuthToken[Access + Refresh]을 재발급한다") {
                val result: AuthToken = sut.reissueAuthorityToken(member.id, member.authority)

                verify(exactly = 1) {
                    tokenProvider.createAccessToken(member.id, member.authority)
                    tokenProvider.createRefreshToken(member.id)
                }
                assertSoftly(result) {
                    accessToken shouldBe ACCESS_TOKEN
                    refreshToken shouldBe REFRESH_TOKEN
                }
            }
        }
    }
})
