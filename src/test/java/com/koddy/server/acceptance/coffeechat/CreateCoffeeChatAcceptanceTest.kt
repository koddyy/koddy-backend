package com.koddy.server.acceptance.coffeechat

import com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘토가_멘티에게_커피챗을_제안한다
import com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘티가_멘토에게_커피챗을_신청한다
import com.koddy.server.auth.domain.model.AuthMember
import com.koddy.server.auth.exception.AuthExceptionCode.INVALID_PERMISSION
import com.koddy.server.common.AcceptanceTestKt
import com.koddy.server.common.containers.callback.DatabaseCleanerEachCallbackExtension
import com.koddy.server.common.fixture.MenteeFixtureStore.menteeFixture
import com.koddy.server.common.fixture.MentorFixtureStore.mentorFixture
import com.koddy.server.common.toLocalDate
import com.koddy.server.common.toLocalDateTime
import com.koddy.server.member.exception.MemberExceptionCode.CANNOT_RESERVATION
import com.koddy.server.member.exception.MemberExceptionCode.MENTOR_NOT_FILL_IN_SCHEDULE
import com.koddy.server.member.presentation.request.MentorScheduleRequest
import com.koddy.server.member.presentation.request.model.MentoringPeriodRequestModel
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.OK

@ExtendWith(DatabaseCleanerEachCallbackExtension::class)
@DisplayName("[Acceptance Test] 커피챗 제안/신청")
internal class CreateCoffeeChatAcceptanceTest : AcceptanceTestKt() {
    companion object {
        private val menteeFixture = menteeFixture(sequence = 1)
        private val mentorFixture = mentorFixture(sequence = 1)
    }

    @Nested
    @DisplayName("멘토 -> 멘티 커피챗 제안 API")
    internal inner class SuggestCoffeeChat {
        @Test
        fun `멘티는 권한이 없다`() {
            // given
            val mentee: AuthMember = menteeFixture(1).회원가입과_로그인을_하고_프로필을_완성시킨다()

            // when - then
            멘토가_멘티에게_커피챗을_제안한다(
                menteeId = mentee.id,
                accessToken = mentee.token.accessToken,
            ).statusCode(FORBIDDEN.value())
                .body("errorCode", `is`(INVALID_PERMISSION.errorCode))
                .body("message", `is`(INVALID_PERMISSION.message))
        }

        @Test
        fun `멘토가 멘티에게 커피챗을 제안한다`() {
            // given
            val mentor: AuthMember = mentorFixture.회원가입과_로그인을_하고_프로필을_완성시킨다()
            val mentee: AuthMember = menteeFixture.회원가입과_로그인을_하고_프로필을_완성시킨다()

            // when - then
            멘토가_멘티에게_커피챗을_제안한다(
                menteeId = mentee.id,
                accessToken = mentor.token.accessToken,
            ).statusCode(OK.value())
                .body("result", notNullValue(Long::class.java))
        }
    }

    @Nested
    @DisplayName("멘티 -> 멘토 커피챗 신청 API")
    internal inner class ApplyCoffeeChat {
        @Test
        fun `멘토는 권한이 없다`() {
            // given
            val mentor: AuthMember = mentorFixture.회원가입과_로그인을_하고_프로필을_완성시킨다()

            // when - then
            멘티가_멘토에게_커피챗을_신청한다(
                start = "2024/2/1-18:00".toLocalDateTime(),
                end = "2024/2/1-18:30".toLocalDateTime(),
                mentorId = mentor.id,
                accessToken = mentor.token.accessToken,
            ).statusCode(FORBIDDEN.value())
                .body("errorCode", `is`(INVALID_PERMISSION.errorCode))
                .body("message", `is`(INVALID_PERMISSION.message))
        }

        @Test
        fun `멘토가 멘토링 관련 정보를 기입하지 않으면 예약할 수 없다`() {
            // given
            val mentee: AuthMember = menteeFixture.회원가입과_로그인을_하고_프로필을_완성시킨다()
            val mentor: AuthMember = mentorFixture.회원가입과_로그인을_진행한다()

            // when - then
            멘티가_멘토에게_커피챗을_신청한다(
                start = "2024/2/1-18:00".toLocalDateTime(),
                end = "2024/2/1-18:30".toLocalDateTime(),
                mentorId = mentor.id,
                accessToken = mentee.token.accessToken,
            ).statusCode(CONFLICT.value())
                .body("errorCode", `is`(MENTOR_NOT_FILL_IN_SCHEDULE.errorCode))
                .body("message", `is`(MENTOR_NOT_FILL_IN_SCHEDULE.message))
        }

        @Test
        fun `멘토링 진행 기간에 포함되지 않으면 예약할 수 없다`() {
            // given
            val mentee: AuthMember = menteeFixture.회원가입과_로그인을_하고_프로필을_완성시킨다()
            val mentor: AuthMember = mentorFixture.회원가입과_로그인을_하고_프로필을_완성시킨다(
                MentoringPeriodRequestModel(
                    startDate = "2024/2/2".toLocalDate(),
                    endDate = "2024/2/20".toLocalDate(),
                ),
            )

            // when - then
            멘티가_멘토에게_커피챗을_신청한다(
                start = "2024/2/1-18:00".toLocalDateTime(),
                end = "2024/2/1-18:30".toLocalDateTime(),
                mentorId = mentor.id,
                accessToken = mentee.token.accessToken,
            ).statusCode(CONFLICT.value())
                .body("errorCode", `is`(CANNOT_RESERVATION.errorCode))
                .body("message", `is`(CANNOT_RESERVATION.message))
        }

        @Test
        fun `멘토링 진행 시간이 멘토가 정한 TimeUnit과 일치하지 않으면 예약할 수 없다 - default = 30분`() {
            // given
            val mentee: AuthMember = menteeFixture.회원가입과_로그인을_하고_프로필을_완성시킨다()
            val mentor: AuthMember = mentorFixture.회원가입과_로그인을_하고_프로필을_완성시킨다()

            // when - then
            멘티가_멘토에게_커피챗을_신청한다(
                start = "2024/2/1-18:00".toLocalDateTime(),
                end = "2024/2/1-19:00".toLocalDateTime(),
                mentorId = mentor.id,
                accessToken = mentee.token.accessToken,
            ).statusCode(CONFLICT.value())
                .body("errorCode", `is`(CANNOT_RESERVATION.errorCode))
                .body("message", `is`(CANNOT_RESERVATION.message))
        }

        @Test
        fun `요일별 스케줄 시간대에 포함되지 않으면 예약할 수 없다`() {
            // given
            val mentee: AuthMember = menteeFixture.회원가입과_로그인을_하고_프로필을_완성시킨다()
            val mentor: AuthMember = mentorFixture.회원가입과_로그인을_하고_프로필을_완성시킨다(
                listOf(
                    MentorScheduleRequest(
                        dayOfWeek = "금",
                        start = MentorScheduleRequest.Start(
                            hour = 9,
                            minute = 0,
                        ),
                        end = MentorScheduleRequest.End(
                            hour = 16,
                            minute = 0,
                        ),
                    ),
                ),
            )

            // when - then
            멘티가_멘토에게_커피챗을_신청한다(
                start = "2024/2/1-16:00".toLocalDateTime(),
                end = "2024/2/1-16:30".toLocalDateTime(),
                mentorId = mentor.id,
                accessToken = mentee.token.accessToken,
            ).statusCode(CONFLICT.value())
                .body("errorCode", `is`(CANNOT_RESERVATION.errorCode))
                .body("message", `is`(CANNOT_RESERVATION.message))
        }

        @Test
        fun `이미 예약된 커피챗 시간대와 겹치면 예약할 수 없다`() {
            // given
            val mentee: AuthMember = menteeFixture.회원가입과_로그인을_하고_프로필을_완성시킨다()
            val mentor: AuthMember = mentorFixture.회원가입과_로그인을_하고_프로필을_완성시킨다()
            멘티가_멘토에게_커피챗을_신청한다(
                start = "2024/2/1-18:00".toLocalDateTime(),
                end = "2024/2/1-18:30".toLocalDateTime(),
                mentorId = mentor.id,
                accessToken = mentee.token.accessToken,
            )

            // when - then
            멘티가_멘토에게_커피챗을_신청한다(
                start = "2024/2/1-17:50".toLocalDateTime(),
                end = "2024/2/1-18:20".toLocalDateTime(),
                mentorId = mentor.id,
                accessToken = mentee.token.accessToken,
            ).statusCode(CONFLICT.value())
                .body("errorCode", `is`(CANNOT_RESERVATION.errorCode))
                .body("message", `is`(CANNOT_RESERVATION.message))
            멘티가_멘토에게_커피챗을_신청한다(
                start = "2024/2/1-18:00".toLocalDateTime(),
                end = "2024/2/1-18:30".toLocalDateTime(),
                mentorId = mentor.id,
                accessToken = mentee.token.accessToken,
            ).statusCode(CONFLICT.value())
                .body("errorCode", `is`(CANNOT_RESERVATION.errorCode))
                .body("message", `is`(CANNOT_RESERVATION.message))
            멘티가_멘토에게_커피챗을_신청한다(
                start = "2024/2/1-18:20".toLocalDateTime(),
                end = "2024/2/1-18:50".toLocalDateTime(),
                mentorId = mentor.id,
                accessToken = mentee.token.accessToken,
            ).statusCode(CONFLICT.value())
                .body("errorCode", `is`(CANNOT_RESERVATION.errorCode))
                .body("message", `is`(CANNOT_RESERVATION.message))
        }

        @Test
        fun `멘티가 멘토에게 커피챗을 신청한다`() {
            // given
            val mentee: AuthMember = menteeFixture.회원가입과_로그인을_하고_프로필을_완성시킨다()
            val mentor: AuthMember = mentorFixture.회원가입과_로그인을_하고_프로필을_완성시킨다()

            // when - then
            멘티가_멘토에게_커피챗을_신청한다(
                start = "2024/2/2-18:00".toLocalDateTime(),
                end = "2024/2/2-18:30".toLocalDateTime(),
                mentorId = mentor.id,
                accessToken = mentee.token.accessToken,
            ).statusCode(OK.value())
                .body("result", notNullValue(Long::class.java))
            멘티가_멘토에게_커피챗을_신청한다(
                start = "2024/2/2-17:30".toLocalDateTime(),
                end = "2024/2/2-18:00".toLocalDateTime(),
                mentorId = mentor.id,
                accessToken = mentee.token.accessToken,
            ).statusCode(OK.value())
                .body("result", notNullValue(Long::class.java))
            멘티가_멘토에게_커피챗을_신청한다(
                start = "2024/2/2-18:30".toLocalDateTime(),
                end = "2024/2/2-19:00".toLocalDateTime(),
                mentorId = mentor.id,
                accessToken = mentee.token.accessToken,
            ).statusCode(OK.value())
                .body("result", notNullValue(Long::class.java))
        }
    }
}
