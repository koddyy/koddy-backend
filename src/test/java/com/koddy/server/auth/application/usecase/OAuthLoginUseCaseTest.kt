package com.koddy.server.auth.application.usecase

import com.koddy.server.auth.application.adapter.OAuthLoginProcessor
import com.koddy.server.auth.application.usecase.command.OAuthLoginCommand
import com.koddy.server.auth.domain.model.AuthMember
import com.koddy.server.auth.domain.model.AuthToken
import com.koddy.server.auth.domain.model.oauth.OAuthProvider
import com.koddy.server.auth.domain.service.TokenIssuer
import com.koddy.server.auth.exception.OAuthUserNotFoundException
import com.koddy.server.auth.infrastructure.social.google.response.GoogleUserResponse
import com.koddy.server.common.UnitTestKt
import com.koddy.server.common.fixture.MentorFixture.MENTOR_1
import com.koddy.server.common.utils.OAuthUtils.AUTHORIZATION_CODE
import com.koddy.server.common.utils.OAuthUtils.REDIRECT_URI
import com.koddy.server.common.utils.OAuthUtils.STATE
import com.koddy.server.common.utils.TokenUtils.ACCESS_TOKEN
import com.koddy.server.common.utils.TokenUtils.REFRESH_TOKEN
import com.koddy.server.member.domain.model.Member
import com.koddy.server.member.domain.service.MemberReader
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

@UnitTestKt
@DisplayName("Auth -> OAuthLoginUseCase 테스트")
internal class OAuthLoginUseCaseTest : DescribeSpec({
    val oAuthLoginProcessor = mockk<OAuthLoginProcessor>()
    val memberReader = mockk<MemberReader>()
    val tokenIssuer = mockk<TokenIssuer>()
    val sut = OAuthLoginUseCase(
        oAuthLoginProcessor,
        memberReader,
        tokenIssuer,
    )

    val member: Member<*> = MENTOR_1.toDomain().apply(1L)

    describe("OAuthLoginUseCase's invoke (구글 로그인)") {
        val command = OAuthLoginCommand(
            provider = OAuthProvider.GOOGLE,
            code = AUTHORIZATION_CODE,
            redirectUrl = REDIRECT_URI,
            state = STATE,
        )
        val googleUserResponse: GoogleUserResponse = MENTOR_1.toGoogleUserResponse()
        every { oAuthLoginProcessor.login(command.provider, command.code, command.redirectUrl, command.state) } returns googleUserResponse

        context("소셜 로그인 사용자가 서버 스토리지에 존재하지 않으면") {
            every { memberReader.findByPlatformSocialId(googleUserResponse.id()) } returns null

            it("OAuthUserNotFoundException 예외가 발생하고 회원가입 플로우를 진행한다") {
                val result: OAuthUserNotFoundException = shouldThrow<OAuthUserNotFoundException> { sut.invoke(command) }

                verify(exactly = 1) { memberReader.findByPlatformSocialId(googleUserResponse.id()) }
                verify(exactly = 0) { tokenIssuer.provideAuthorityToken(member.id, member.authority) }
                assertSoftly(result.response) {
                    id() shouldBe googleUserResponse.id()
                    name() shouldBe googleUserResponse.name()
                    email() shouldBe googleUserResponse.email()
                    profileImageUrl() shouldBe googleUserResponse.profileImageUrl()
                }
            }
        }

        context("소셜 로그인 사용자가 서버 스토리지에 존재하면") {
            every { memberReader.findByPlatformSocialId(googleUserResponse.id()) } returns member

            val authToken = AuthToken(ACCESS_TOKEN, REFRESH_TOKEN)
            every { tokenIssuer.provideAuthorityToken(member.id, member.authority) } returns authToken

            it("로그인 처리를 진행한다") {
                val result: AuthMember = sut.invoke(command)

                verify(exactly = 1) {
                    memberReader.findByPlatformSocialId(googleUserResponse.id())
                    tokenIssuer.provideAuthorityToken(member.id, member.authority)
                }
                assertSoftly(result) {
                    id shouldBe member.id
                    name shouldBe member.name
                    token.accessToken shouldBe ACCESS_TOKEN
                    token.refreshToken shouldBe REFRESH_TOKEN
                }
            }
        }
    }
})
