package com.koddy.server.member.application.usecase

import com.koddy.server.auth.domain.model.code.AuthCodeGenerator
import com.koddy.server.auth.domain.model.code.AuthKeyGenerator
import com.koddy.server.auth.exception.AuthException
import com.koddy.server.auth.exception.AuthExceptionCode.INVALID_AUTH_CODE
import com.koddy.server.common.IntegrateTestKt
import com.koddy.server.common.fixture.MentorFixture.MENTOR_1
import com.koddy.server.common.mock.stub.StubEmailSender
import com.koddy.server.global.utils.redis.RedisOperator
import com.koddy.server.mail.application.adapter.EmailSender
import com.koddy.server.member.application.usecase.command.AttemptWithMailCommand
import com.koddy.server.member.application.usecase.command.AttemptWithProofDataCommand
import com.koddy.server.member.application.usecase.command.ConfirmMailAuthCodeCommand
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.model.mentor.UniversityAuthentication
import com.koddy.server.member.domain.repository.MentorRepository
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import okhttp3.internal.format
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.data.repository.findByIdOrNull

@IntegrateTestKt
@Import(AuthenticateMentorUnivUseCaseIntegrateTest.FakeConfig::class)
@DisplayName("Member -> AuthenticateMentorUnivUseCase 테스트 [IntegrateTest]")
internal class AuthenticateMentorUnivUseCaseIntegrateTest(
    private val sut: AuthenticateMentorUnivUseCase,
    private val mentorRepository: MentorRepository,
    private val redisOperator: RedisOperator<String, String>,
) {
    @TestConfiguration
    internal class FakeConfig {
        @Bean
        @Primary
        fun authKeyGenerator(): AuthKeyGenerator = AuthKeyGenerator { _: String, suffix: Array<out Any> -> format(AUTH_KEY_PREFIX, *suffix) }

        @Bean
        @Primary
        fun authCodeGenerator(): AuthCodeGenerator = AuthCodeGenerator { AUTH_CODE }

        @Bean
        @Primary
        fun emailSender(): EmailSender = StubEmailSender()
    }

    @Nested
    @DisplayName("메일 인증 시도")
    internal inner class AttemptWithMail {
        @Test
        fun `학교 메일로 멘토 인증을 시도한다`() {
            // given
            val mentor: Mentor = mentorRepository.save(MENTOR_1.toDomain())
            val schoolMail = "sjiwon@kyonggi.ac.kr"
            val command = AttemptWithMailCommand(
                mentor.id,
                schoolMail,
            )

            // when
            sut.attemptWithMail(command)

            // then
            val findMentor: Mentor = mentorRepository.findByIdOrNull(mentor.id)!!
            assertSoftly {
                findMentor.universityAuthentication.schoolMail shouldBe command.schoolMail
                findMentor.universityAuthentication.proofDataUploadUrl shouldBe null
                findMentor.universityAuthentication.status shouldBe UniversityAuthentication.AuthenticationStatus.ATTEMPT

                redisOperator.get(getAuthKey(mentor.id, command.schoolMail)) shouldBe AUTH_CODE
            }
        }
    }

    @Nested
    @DisplayName("메일 인증번호 확인")
    internal inner class ConfirmMailAuthCode {
        @Test
        fun `인증번호가 일치하지 않으면 인증에 실패한다`() {
            // given
            val mentor: Mentor = mentorRepository.save(MENTOR_1.toDomain())
            val schoolMail = "sjiwon@kyonggi.ac.kr"
            sut.attemptWithMail(
                AttemptWithMailCommand(
                    mentor.id,
                    schoolMail,
                ),
            )

            val command = ConfirmMailAuthCodeCommand(
                mentor.id,
                schoolMail,
                "${AUTH_CODE}_diff",
            )

            // when - then
            shouldThrow<AuthException> {
                sut.confirmMailAuthCode(command)
            } shouldHaveMessage INVALID_AUTH_CODE.message

            val findMentor: Mentor = mentorRepository.findByIdOrNull(mentor.id)!!
            assertSoftly {
                findMentor.universityAuthentication.schoolMail shouldBe command.schoolMail
                findMentor.universityAuthentication.proofDataUploadUrl shouldBe null
                findMentor.universityAuthentication.status shouldBe UniversityAuthentication.AuthenticationStatus.ATTEMPT

                redisOperator.get(getAuthKey(mentor.id, command.schoolMail)) shouldBe AUTH_CODE
            }
        }

        @Test
        fun `인증번호가 일치하면 인증에 성공한다`() {
            // given
            val mentor: Mentor = mentorRepository.save(MENTOR_1.toDomain())
            val schoolMail = "sjiwon@kyonggi.ac.kr"
            sut.attemptWithMail(
                AttemptWithMailCommand(
                    mentor.id,
                    schoolMail,
                ),
            )

            val command = ConfirmMailAuthCodeCommand(
                mentor.id,
                schoolMail,
                AUTH_CODE,
            )

            // when
            sut.confirmMailAuthCode(command)

            // then
            val findMentor: Mentor = mentorRepository.findByIdOrNull(mentor.id)!!
            assertSoftly {
                findMentor.universityAuthentication.schoolMail shouldBe command.schoolMail
                findMentor.universityAuthentication.proofDataUploadUrl shouldBe null
                findMentor.universityAuthentication.status shouldBe UniversityAuthentication.AuthenticationStatus.SUCCESS

                redisOperator.get(getAuthKey(mentor.id, command.schoolMail)) shouldBe null
            }
        }
    }

    @Nested
    @DisplayName("증명 자료 인증 시도")
    internal inner class AttemptWithProofData {
        @Test
        fun `증명 자료로 멘토 인증을 시도한다`() {
            // given
            val mentor: Mentor = mentorRepository.save(MENTOR_1.toDomain())
            val command = AttemptWithProofDataCommand(
                mentor.id,
                "https://proof-data-url",
            )

            // when
            sut.attemptWithProofData(command)

            // then
            val findMentor: Mentor = mentorRepository.findByIdOrNull(mentor.id)!!
            assertSoftly(findMentor) {
                universityAuthentication.schoolMail shouldBe null
                universityAuthentication.proofDataUploadUrl shouldBe command.proofDataUploadUrl
                universityAuthentication.status shouldBe UniversityAuthentication.AuthenticationStatus.ATTEMPT
            }
        }
    }

    private fun getAuthKey(memberId: Long, schoolMail: String): String {
        return String.format(AUTH_KEY_PREFIX, memberId, schoolMail)
    }

    companion object {
        private const val AUTH_KEY_PREFIX = "MENTOR-MAIL-AUTH:%d:%s"
        private const val AUTH_CODE = "123456"
    }
}
