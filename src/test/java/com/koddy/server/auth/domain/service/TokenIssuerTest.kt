package com.koddy.server.auth.domain.service

import com.koddy.server.auth.application.adapter.TokenStore
import com.koddy.server.auth.domain.model.AuthToken
import com.koddy.server.common.UnitTestKt
import com.koddy.server.common.fixture.MentorFixture.MENTOR_1
import com.koddy.server.common.utils.TokenUtils.ACCESS_TOKEN
import com.koddy.server.common.utils.TokenUtils.REFRESH_TOKEN
import com.koddy.server.member.domain.model.Member
import io.kotest.assertions.assertSoftly
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify

@UnitTestKt
@DisplayName("Auth -> TokenIssuer 테스트")
internal class TokenIssuerTest : BehaviorSpec({
    val tokenProvider = mockk<TokenProvider>()
    val tokenStore = mockk<TokenStore>()
    val sut = TokenIssuer(tokenProvider, tokenStore)

    Given("인증에 성공한 사용자가") {
        val member: Member<*> = MENTOR_1.toDomain().apply(1L)

        When("토큰 발급을 요청하면") {
            every { tokenProvider.createAccessToken(member.id, member.authority) } returns ACCESS_TOKEN
            every { tokenProvider.createRefreshToken(member.id) } returns REFRESH_TOKEN
            justRun { tokenStore.synchronizeRefreshToken(member.id, REFRESH_TOKEN) }

            Then("AuthToken[Access + Refresh]을 제공한다") {
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

    Given("AccessToken이 만료된 사용자가") {
        val member: Member<*> = MENTOR_1.toDomain().apply(1L)

        When("본인 소유의 RefreshToken을 통해서 재발급을 요청하면") {
            every { tokenProvider.createAccessToken(member.id, member.authority) } returns ACCESS_TOKEN
            every { tokenProvider.createRefreshToken(member.id) } returns REFRESH_TOKEN
            justRun { tokenStore.updateRefreshToken(member.id, REFRESH_TOKEN) }

            Then("AuthToken[Access + Refresh]을 재발급한다") {
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
