package com.koddy.server.member.application.usecase

import com.koddy.server.auth.domain.model.AuthMember
import com.koddy.server.auth.domain.model.AuthToken
import com.koddy.server.auth.domain.service.TokenIssuer
import com.koddy.server.common.UnitTestKt
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_1
import com.koddy.server.common.fixture.MentorFixture.MENTOR_1
import com.koddy.server.common.utils.TokenUtils.ACCESS_TOKEN
import com.koddy.server.common.utils.TokenUtils.REFRESH_TOKEN
import com.koddy.server.member.application.usecase.command.SignUpMenteeCommand
import com.koddy.server.member.application.usecase.command.SignUpMentorCommand
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.repository.MemberRepository
import com.koddy.server.member.exception.MemberException
import com.koddy.server.member.exception.MemberExceptionCode.ACCOUNT_ALREADY_EXISTS
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
@DisplayName("Member -> SignUpUseCase 테스트")
internal class SignUpUseCaseTest : DescribeSpec({
    val memberRepository = mockk<MemberRepository>()
    val tokenIssuer = mockk<TokenIssuer>()
    val sut = SignUpUseCase(
        memberRepository,
        tokenIssuer,
    )

    describe("SignUpUsecase's signUpMentor") {
        val command = SignUpMentorCommand(
            MENTOR_1.platform,
            MENTOR_1.getName(),
            MENTOR_1.languages,
            MENTOR_1.universityProfile,
        )

        context("이미 가입된 소셜 계정 데이터면") {
            every { memberRepository.existsByPlatformSocialId(command.platform.socialId) } returns true

            it("멘토는 중복 회원가입이 불가능하다") {
                shouldThrow<MemberException> {
                    sut.signUpMentor(command)
                } shouldHaveMessage ACCOUNT_ALREADY_EXISTS.message

                verify(exactly = 1) { memberRepository.existsByPlatformSocialId(command.platform.socialId) }
                verify(exactly = 0) {
                    memberRepository.save(any(Mentor::class))
                    tokenIssuer.provideAuthorityToken(any(Long::class), any(String::class))
                }
            }
        }

        context("가입되지 않은 소셜 계정 데이터면") {
            every { memberRepository.existsByPlatformSocialId(command.platform.socialId) } returns false

            val mentor: Mentor = MENTOR_1.toDomain().apply(1L)
            every { memberRepository.save(any()) } returns mentor

            val authToken = AuthToken(ACCESS_TOKEN, REFRESH_TOKEN)
            every { tokenIssuer.provideAuthorityToken(mentor.id, mentor.authority) } returns authToken

            it("멘토는 회원가입 + 로그인 처리를 진행한다") {
                val result: AuthMember = sut.signUpMentor(command)

                verify(exactly = 1) {
                    memberRepository.existsByPlatformSocialId(command.platform.socialId)
                    memberRepository.save(any(Mentor::class))
                    tokenIssuer.provideAuthorityToken(mentor.id, mentor.authority)
                }
                assertSoftly(result) {
                    id shouldBe mentor.id
                    name shouldBe mentor.name
                    token.accessToken shouldBe ACCESS_TOKEN
                    token.refreshToken shouldBe REFRESH_TOKEN
                }
            }
        }
    }

    describe("SignUpUsecase's signUpMentee") {
        val command = SignUpMenteeCommand(
            MENTEE_1.platform,
            MENTEE_1.getName(),
            MENTEE_1.nationality,
            MENTEE_1.languages,
            MENTEE_1.interest,
        )

        context("이미 가입된 소셜 계정 데이터면") {
            every { memberRepository.existsByPlatformSocialId(command.platform.socialId) } returns true

            it("멘티는 중복 회원가입이 불가능하다") {
                shouldThrow<MemberException> {
                    sut.signUpMentee(command)
                } shouldHaveMessage ACCOUNT_ALREADY_EXISTS.message

                verify(exactly = 1) { memberRepository.existsByPlatformSocialId(command.platform.socialId) }
                verify(exactly = 0) {
                    memberRepository.save(any(Mentee::class))
                    tokenIssuer.provideAuthorityToken(any(Long::class), any(String::class))
                }
            }
        }

        context("가입되지 않은 소셜 계정 데이터면") {
            every { memberRepository.existsByPlatformSocialId(command.platform.socialId) } returns false

            val mentee: Mentee = MENTEE_1.toDomain().apply(1L)
            every { memberRepository.save(any()) } returns mentee

            val authToken = AuthToken(ACCESS_TOKEN, REFRESH_TOKEN)
            every { tokenIssuer.provideAuthorityToken(mentee.id, mentee.authority) } returns authToken

            it("멘티는 회원가입 + 로그인 처리를 진행한다") {
                val result: AuthMember = sut.signUpMentee(command)

                verify(exactly = 1) {
                    memberRepository.existsByPlatformSocialId(command.platform.socialId)
                    memberRepository.save(any(Mentee::class))
                    tokenIssuer.provideAuthorityToken(mentee.id, mentee.authority)
                }
                assertSoftly(result) {
                    id shouldBe mentee.id
                    name shouldBe mentee.name
                    token.accessToken shouldBe ACCESS_TOKEN
                    token.refreshToken shouldBe REFRESH_TOKEN
                }
            }
        }
    }
})
