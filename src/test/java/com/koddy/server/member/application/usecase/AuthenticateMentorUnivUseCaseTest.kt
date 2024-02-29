package com.koddy.server.member.application.usecase

import com.koddy.server.auth.application.adapter.AuthenticationProcessor
import com.koddy.server.auth.domain.model.code.AuthKeyGenerator
import com.koddy.server.auth.exception.AuthException
import com.koddy.server.auth.exception.AuthExceptionCode.INVALID_AUTH_CODE
import com.koddy.server.common.UnitTestKt
import com.koddy.server.common.fixture.MentorFixture.MENTOR_1
import com.koddy.server.member.application.usecase.command.AttemptWithMailCommand
import com.koddy.server.member.application.usecase.command.AttemptWithProofDataCommand
import com.koddy.server.member.application.usecase.command.ConfirmMailAuthCodeCommand
import com.koddy.server.member.domain.event.MailAuthenticatedEvent
import com.koddy.server.member.domain.model.mentor.AuthenticationStatus
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.repository.MentorRepository
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import okhttp3.internal.format
import org.springframework.context.ApplicationEventPublisher

@UnitTestKt
@DisplayName("Member -> AuthenticationMentorUnivUseCase 테스트")
internal class AuthenticateMentorUnivUseCaseTest : DescribeSpec({
    val authKeyPrefix = "MENTOR-MAIL-AUTH:%d:%s"
    val authCode = "123456"

    val mentorRepository = mockk<MentorRepository>()
    val authKeyGenerator = AuthKeyGenerator { _: String, suffix: Array<out Any> -> format(authKeyPrefix, *suffix) }
    val authenticationProcessor = mockk<AuthenticationProcessor>()
    val eventPublisher = mockk<ApplicationEventPublisher>()
    val sut = AuthenticateMentorUnivUseCase(
        mentorRepository,
        authKeyGenerator,
        authenticationProcessor,
        eventPublisher,
    )

    val mentor: Mentor = MENTOR_1.toDomain().apply(1L)
    val schoolMail = "sjiwon@kyonggi.ac.kr"

    fun getAuthKey(
        memberId: Long,
        schoolMail: String,
    ): String = String.format(authKeyPrefix, memberId, schoolMail)

    describe("AuthenticationMentorUnivUseCase's attemptWithMail") {
        context("로그인한 멘토 사용자는 자신의 학교 메일을 통해서") {
            val command = AttemptWithMailCommand(
                mentor.id,
                schoolMail,
            )
            every { mentorRepository.getById(command.mentorId) } returns mentor
            every { authenticationProcessor.storeAuthCode(getAuthKey(mentor.id, command.schoolMail)) } returns authCode

            val slotEvent = slot<MailAuthenticatedEvent>()
            justRun { eventPublisher.publishEvent(capture(slotEvent)) }

            it("인증을 시도할 수 있다") {
                sut.attemptWithMail(command)

                verify(exactly = 1) {
                    mentorRepository.getById(command.mentorId)
                    authenticationProcessor.storeAuthCode(getAuthKey(mentor.id, command.schoolMail))
                }
                slotEvent.captured shouldBe MailAuthenticatedEvent(
                    mentorId = mentor.id,
                    targetEmail = schoolMail,
                    authCode = authCode,
                )
                assertSoftly(mentor) {
                    universityAuthentication.schoolMail shouldBe command.schoolMail
                    universityAuthentication.proofDataUploadUrl shouldBe null
                    universityAuthentication.status shouldBe AuthenticationStatus.ATTEMPT
                }
            }
        }
    }

    describe("AuthenticationMentorUnivUseCase's confirmMailAuthCode") {
        mentor.authWithMail(schoolMail)

        context("메일로 받은 인증번호를 잘못 기입하면") {
            val command = ConfirmMailAuthCodeCommand(
                mentor.id,
                schoolMail,
                authCode,
            )
            every { mentorRepository.getById(command.mentorId) } returns mentor
            every { authenticationProcessor.verifyAuthCode(getAuthKey(mentor.id, command.schoolMail), command.authCode) } throws AuthException(INVALID_AUTH_CODE)

            it("인증에 실패한다") {
                shouldThrow<AuthException> {
                    sut.confirmMailAuthCode(command)
                } shouldHaveMessage INVALID_AUTH_CODE.message

                verify(exactly = 1) {
                    mentorRepository.getById(command.mentorId)
                    authenticationProcessor.verifyAuthCode(getAuthKey(mentor.id, command.schoolMail), command.authCode)
                }
                verify(exactly = 0) { authenticationProcessor.deleteAuthCode(getAuthKey(mentor.id, command.schoolMail)) }
                assertSoftly(mentor) {
                    universityAuthentication.schoolMail shouldBe command.schoolMail
                    universityAuthentication.proofDataUploadUrl shouldBe null
                    universityAuthentication.status shouldBe AuthenticationStatus.ATTEMPT
                }
            }
        }

        context("메일로 받은 인증번호를 정상적으로 기입하면") {
            val command = ConfirmMailAuthCodeCommand(
                mentor.id,
                schoolMail,
                authCode,
            )
            every { mentorRepository.getById(command.mentorId) } returns mentor
            justRun { authenticationProcessor.verifyAuthCode(getAuthKey(mentor.id, command.schoolMail), command.authCode) }
            justRun { authenticationProcessor.deleteAuthCode(getAuthKey(mentor.id, command.schoolMail)) }

            it("인증에 성공한다") {
                sut.confirmMailAuthCode(command)

                verify(exactly = 1) {
                    mentorRepository.getById(command.mentorId)
                    authenticationProcessor.verifyAuthCode(getAuthKey(mentor.id, command.schoolMail), command.authCode)
                    authenticationProcessor.deleteAuthCode(getAuthKey(mentor.id, command.schoolMail))
                }
                assertSoftly(mentor) {
                    universityAuthentication.schoolMail shouldBe command.schoolMail
                    universityAuthentication.proofDataUploadUrl shouldBe null
                    universityAuthentication.status shouldBe AuthenticationStatus.SUCCESS
                }
            }
        }
    }

    describe("AuthenticationMentorUnivUseCase's attemptWithProofData") {
        context("로그인한 멘토 사용자는 증명 자료를 통해서") {
            val command = AttemptWithProofDataCommand(
                mentor.id,
                "https://proof-data-url",
            )
            every { mentorRepository.getById(command.mentorId) } returns mentor

            it("인증을 시도할 수 있다") {
                sut.attemptWithProofData(command)

                verify(exactly = 1) { mentorRepository.getById(command.mentorId) }
                assertSoftly(mentor) {
                    universityAuthentication.schoolMail shouldBe null
                    universityAuthentication.proofDataUploadUrl shouldBe command.proofDataUploadUrl
                    universityAuthentication.status shouldBe AuthenticationStatus.ATTEMPT
                }
            }
        }
    }
})
