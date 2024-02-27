package com.koddy.server.auth.application.usecase

import com.koddy.server.auth.domain.model.AuthToken
import com.koddy.server.auth.domain.service.TokenIssuer
import com.koddy.server.auth.domain.service.TokenProvider
import com.koddy.server.auth.exception.AuthException
import com.koddy.server.auth.exception.AuthExceptionCode.INVALID_TOKEN
import com.koddy.server.common.UnitTestKt
import com.koddy.server.common.fixture.MentorFixture.MENTOR_1
import com.koddy.server.common.utils.TokenUtils.ACCESS_TOKEN
import com.koddy.server.common.utils.TokenUtils.REFRESH_TOKEN
import com.koddy.server.member.domain.model.Member
import com.koddy.server.member.domain.repository.MemberRepository
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

@UnitTestKt
@DisplayName("Auth -> ReissueTokenUseCase 테스트")
internal class ReissueTokenUseCaseTest : DescribeSpec({
    val memberRepository = mockk<MemberRepository>()
    val tokenProvider = mockk<TokenProvider>()
    val tokenIssuer = mockk<TokenIssuer>()
    val sut = ReissueTokenUseCase(
        memberRepository,
        tokenProvider,
        tokenIssuer,
    )

    describe("ReissueTokenUseCase's invoke") {
        val member: Member<*> = MENTOR_1.toDomain().apply(1L)

        every { tokenProvider.getId(REFRESH_TOKEN) } returns member.id
        every { memberRepository.getById(member.id) } returns member

        context("자신이 소유한 RefreshToken이 아니면") {
            every { tokenIssuer.isMemberRefreshToken(member.id, REFRESH_TOKEN) } returns false

            it("INVALID_TOKEN 예외가 발생하고 토큰 재발급에 실패한다") {
                shouldThrow<AuthException> {
                    sut.invoke(REFRESH_TOKEN)
                } shouldHaveMessage INVALID_TOKEN.message

                verify(exactly = 1) {
                    tokenProvider.getId(REFRESH_TOKEN)
                    memberRepository.getById(member.id)
                    tokenIssuer.isMemberRefreshToken(member.id, REFRESH_TOKEN)
                }
                verify(exactly = 0) { tokenIssuer.reissueAuthorityToken(member.id, member.authority) }
            }
        }

        context("자신이 소유한 RefreshToken이 맞으면") {
            every { tokenIssuer.isMemberRefreshToken(member.id, REFRESH_TOKEN) } returns true

            val authToken = AuthToken(ACCESS_TOKEN, REFRESH_TOKEN)
            every { tokenIssuer.reissueAuthorityToken(member.id, member.authority) } returns authToken

            it("토큰 재발급을 진행한다") {
                val result: AuthToken = sut.invoke(REFRESH_TOKEN)

                verify(exactly = 1) {
                    tokenProvider.getId(REFRESH_TOKEN)
                    memberRepository.getById(member.id)
                    tokenIssuer.isMemberRefreshToken(member.id, REFRESH_TOKEN)
                    tokenIssuer.reissueAuthorityToken(member.id, member.authority)
                }
                assertSoftly(result) {
                    accessToken shouldBe ACCESS_TOKEN
                    refreshToken shouldBe REFRESH_TOKEN
                }
            }
        }
    }
})
