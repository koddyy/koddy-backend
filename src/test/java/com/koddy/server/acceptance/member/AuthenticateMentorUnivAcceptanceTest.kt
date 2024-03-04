package com.koddy.server.acceptance.member

import com.koddy.server.acceptance.member.MemberAcceptanceStep.멘토_기본_프로필을_조회한다
import com.koddy.server.acceptance.member.MemberAcceptanceStep.멘토가_메일을_통해서_학교_인증을_시도한다
import com.koddy.server.acceptance.member.MemberAcceptanceStep.멘토가_증명자료를_통해서_학교_인증을_시도한다
import com.koddy.server.acceptance.member.MemberAcceptanceStep.멘토가_학교_메일로_발송된_인증번호를_제출한다
import com.koddy.server.auth.domain.model.AuthMember
import com.koddy.server.auth.exception.AuthExceptionCode.INVALID_AUTH_CODE
import com.koddy.server.auth.exception.AuthExceptionCode.INVALID_PERMISSION
import com.koddy.server.auth.exception.AuthExceptionCode.TOO_MANY_MAIL_AUTH_ATTEMPTS
import com.koddy.server.common.AcceptanceTestKt
import com.koddy.server.common.config.BlackboxLogicControlConfig
import com.koddy.server.common.containers.callback.DatabaseCleanerEachCallbackExtension
import com.koddy.server.common.containers.callback.RedisCleanerEachCallbackExtension
import com.koddy.server.common.fixture.MenteeFixtureStore.menteeFixture
import com.koddy.server.common.fixture.MentorFixtureStore.mentorFixture
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.HttpStatus.OK

@ExtendWith(
    DatabaseCleanerEachCallbackExtension::class,
    RedisCleanerEachCallbackExtension::class,
)
@DisplayName("[Acceptance Test] 멘토 학교 인증")
internal class AuthenticateMentorUnivAcceptanceTest : AcceptanceTestKt() {

    companion object {
        private const val SCHOOL_MAIL = "sjiwon@kyonggi.ac.kr"
        private val menteeFixture = menteeFixture(sequence = 1)
        private val mentorFixture = mentorFixture(sequence = 1)
    }

    @Nested
    @DisplayName("메일 인증 시도 API")
    internal inner class AuthWithMail {
        @Test
        fun `학교 인증은 멘토 대상이므로 멘티는 권한이 없다`() {
            // given
            val mentee: AuthMember = menteeFixture.회원가입과_로그인을_진행한다()

            // when - then
            멘토가_메일을_통해서_학교_인증을_시도한다(SCHOOL_MAIL, mentee.token.accessToken)
                .statusCode(FORBIDDEN.value())
                .body("errorCode", `is`(INVALID_PERMISSION.errorCode))
                .body("message", `is`(INVALID_PERMISSION.message))
        }

        @Test
        fun `메일을 통해서 학교 인증을 시도한다`() {
            // given
            val member: AuthMember = mentorFixture.회원가입과_로그인을_진행한다()

            // when - then
            멘토가_메일을_통해서_학교_인증을_시도한다(
                schoolMail = SCHOOL_MAIL,
                accessToken = member.token.accessToken,
            ).statusCode(NO_CONTENT.value())
        }

        @Test
        fun `짧은 시간동안 3회 이상 인증 시도를 하게 되면 10분동안 인증 시도 밴을 당한다 (HTTP Status 429)`() {
            // given
            val member: AuthMember = mentorFixture.회원가입과_로그인을_진행한다()

            // when - then
            멘토가_메일을_통해서_학교_인증을_시도한다(SCHOOL_MAIL, member.token.accessToken).statusCode(NO_CONTENT.value())
            멘토가_메일을_통해서_학교_인증을_시도한다(SCHOOL_MAIL, member.token.accessToken).statusCode(NO_CONTENT.value())
            멘토가_메일을_통해서_학교_인증을_시도한다(SCHOOL_MAIL, member.token.accessToken).statusCode(NO_CONTENT.value())

            멘토가_메일을_통해서_학교_인증을_시도한다(SCHOOL_MAIL, member.token.accessToken)
                .statusCode(HttpStatus.TOO_MANY_REQUESTS.value())
                .body("errorCode", `is`(TOO_MANY_MAIL_AUTH_ATTEMPTS.errorCode))
                .body("message", `is`(TOO_MANY_MAIL_AUTH_ATTEMPTS.message))
        }
    }

    @Nested
    @DisplayName("메일 인증번호 확인 API")
    internal inner class ConfirmMailAuthCode {
        @Test
        fun `인증번호가 일치하지 않으면 인증에 실패한다`() {
            // given
            val member: AuthMember = mentorFixture.회원가입과_로그인을_진행한다()
            멘토가_메일을_통해서_학교_인증을_시도한다(SCHOOL_MAIL, member.token.accessToken)

            // when - then
            멘토가_학교_메일로_발송된_인증번호를_제출한다(
                schoolMail = SCHOOL_MAIL,
                authCode = "${BlackboxLogicControlConfig.AUTH_CODE}7",
                accessToken = member.token.accessToken,
            ).statusCode(CONFLICT.value())
                .body("errorCode", `is`(INVALID_AUTH_CODE.errorCode))
                .body("message", `is`(INVALID_AUTH_CODE.message))
        }

        @Test
        fun `인증번호가 일치하면 인증에 성공한다`() {
            // given
            val mentor: AuthMember = mentorFixture.회원가입과_로그인을_진행한다()
            멘토가_메일을_통해서_학교_인증을_시도한다(SCHOOL_MAIL, mentor.token.accessToken)

            멘토_기본_프로필을_조회한다(mentor.id)
                .statusCode(OK.value())
                .body("authenticated", `is`(false))

            // when - then
            멘토가_학교_메일로_발송된_인증번호를_제출한다(
                schoolMail = SCHOOL_MAIL,
                authCode = BlackboxLogicControlConfig.AUTH_CODE,
                accessToken = mentor.token.accessToken,
            ).statusCode(NO_CONTENT.value())

            멘토_기본_프로필을_조회한다(mentor.id)
                .statusCode(OK.value())
                .body("authenticated", `is`(true))
        }
    }

    @Nested
    @DisplayName("증명자료 인증 시도 API")
    internal inner class AuthWithProofData {
        @Test
        fun `학교 인증은 멘토 대상이므로 멘티는 권한이 없다`() {
            // given
            val mentee: AuthMember = menteeFixture.회원가입과_로그인을_진행한다()

            // when - then
            멘토가_증명자료를_통해서_학교_인증을_시도한다(mentee.token.accessToken)
                .statusCode(FORBIDDEN.value())
                .body("errorCode", `is`(INVALID_PERMISSION.errorCode))
                .body("message", `is`(INVALID_PERMISSION.message))
        }

        @Test
        fun `증명자료를 통해서 학교 인증을 시도한다`() {
            // given
            val member: AuthMember = mentorFixture.회원가입과_로그인을_진행한다()

            // when - then
            멘토가_증명자료를_통해서_학교_인증을_시도한다(member.token.accessToken).statusCode(NO_CONTENT.value())
        }
    }
}
