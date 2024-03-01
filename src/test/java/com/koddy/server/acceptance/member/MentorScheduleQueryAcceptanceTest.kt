package com.koddy.server.acceptance.member

import com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘토가_Pending_상태인_커피챗에_대해서_최종_수락을_한다
import com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘토가_Pending_상태인_커피챗에_대해서_최종_취소를_한다
import com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다
import com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘토가_멘티의_커피챗_신청을_수락한다
import com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다
import com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘티가_멘토의_커피챗_제안을_1차_수락한다
import com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘티가_멘토의_커피챗_제안을_거절한다
import com.koddy.server.acceptance.member.MemberAcceptanceStep.멘토의_예약된_스케줄_정보를_조회한다
import com.koddy.server.auth.domain.model.AuthMember
import com.koddy.server.common.AcceptanceTestKt
import com.koddy.server.common.containers.callback.DatabaseCleanerEachCallbackExtension
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_1
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_10
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_2
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_3
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_4
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_5
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_6
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_7
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_8
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_9
import com.koddy.server.common.fixture.MentorFixture
import com.koddy.server.common.fixture.MentorFixture.MENTOR_1
import com.koddy.server.common.fixture.StrategyFixture
import com.koddy.server.member.domain.model.mentor.MentoringPeriod
import io.restassured.response.ValidatableResponse
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus.OK
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME

@ExtendWith(DatabaseCleanerEachCallbackExtension::class)
@DisplayName("[Acceptance Test] 멘토 예약된 스케줄 조회")
internal class MentorScheduleQueryAcceptanceTest : AcceptanceTestKt() {
    @Nested
    @DisplayName("특정 Year-Month에 대해서 멘토의 예약된 스케줄 정보를 조회한다")
    internal inner class GetMentorReservedSchedule {
        @Test
        @DisplayName("멘토링 시간 정보를 기입하지 않은 멘토에 대한 조회")
        fun successA() {
            // given
            val mentor: AuthMember = MENTOR_1.회원가입과_로그인을_진행한다()
            val mentee: AuthMember = MENTEE_1.회원가입과_로그인을_하고_프로필을_완성시킨다()

            // when - then
            멘토의_예약된_스케줄_정보를_조회한다(
                mentorId = mentor.id,
                year = 2024,
                month = 2,
                accessToken = mentee.token.accessToken,
            ).statusCode(OK.value())
                .body("period", nullValue())
                .body("schedules", empty<Int>())
                .body("timeUnit", nullValue())
                .body("reserved", empty<Int>())
        }

        @Test
        @DisplayName("멘토링 시간 정보를 기입한 멘토에 대한 조회 + 예약 정보 X")
        fun successB() {
            // given
            val mentor: AuthMember = MENTOR_1.회원가입과_로그인을_하고_프로필을_완성시킨다()
            val mentee: AuthMember = MENTEE_1.회원가입과_로그인을_하고_프로필을_완성시킨다()

            // when - then
            멘토의_예약된_스케줄_정보를_조회한다(
                mentorId = mentor.id,
                year = 2024,
                month = 2,
                accessToken = mentee.token.accessToken,
            ).statusCode(OK.value())
                .body("period.startDate", `is`(MENTOR_1.mentoringPeriod.startDate.toString()))
                .body("period.endDate", `is`(MENTOR_1.mentoringPeriod.endDate.toString()))
                .body("schedules", hasSize<Int>(MENTOR_1.timelines.size))
                .body("timeUnit", `is`(MentoringPeriod.TimeUnit.HALF_HOUR.value))
                .body("reserved", empty<Int>())
        }

        @Test
        @DisplayName("멘토링 시간 정보를 기입한 멘토에 대한 조회 + 예약 정보 O")
        fun successC() {
            // given
            val mentor: AuthMember = MENTOR_1.회원가입과_로그인을_하고_프로필을_완성시킨다()
            val mentees: List<AuthMember> = listOf(
                MENTEE_1.회원가입과_로그인을_하고_프로필을_완성시킨다(),
                MENTEE_2.회원가입과_로그인을_하고_프로필을_완성시킨다(),
                MENTEE_3.회원가입과_로그인을_하고_프로필을_완성시킨다(),
                MENTEE_4.회원가입과_로그인을_하고_프로필을_완성시킨다(),
                MENTEE_5.회원가입과_로그인을_하고_프로필을_완성시킨다(),
                MENTEE_6.회원가입과_로그인을_하고_프로필을_완성시킨다(),
                MENTEE_7.회원가입과_로그인을_하고_프로필을_완성시킨다(),
                MENTEE_8.회원가입과_로그인을_하고_프로필을_완성시킨다(),
                MENTEE_9.회원가입과_로그인을_하고_프로필을_완성시킨다(),
                MENTEE_10.회원가입과_로그인을_하고_프로필을_완성시킨다(),
            )
            val coffeeChats: List<Long> = listOf(
                멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(mentees[0].id, mentor.token.accessToken),
                멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(mentees[1].id, mentor.token.accessToken),
                멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(mentees[2].id, mentor.token.accessToken),
                멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다(
                    LocalDateTime.of(2024, 2, 19, 18, 0),
                    LocalDateTime.of(2024, 2, 19, 18, 30),
                    mentor.id,
                    mentees[3].token.accessToken,
                ),
                멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다(
                    LocalDateTime.of(2024, 3, 4, 18, 0),
                    LocalDateTime.of(2024, 3, 4, 18, 30),
                    mentor.id,
                    mentees[4].token.accessToken,
                ),
                멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(mentees[5].id, mentor.token.accessToken),
                멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(mentees[6].id, mentor.token.accessToken),
                멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다(
                    LocalDateTime.of(2024, 4, 5, 18, 0),
                    LocalDateTime.of(2024, 4, 5, 18, 30),
                    mentor.id,
                    mentees[7].token.accessToken,
                ),
                멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다(
                    LocalDateTime.of(2024, 4, 17, 18, 0),
                    LocalDateTime.of(2024, 4, 17, 18, 30),
                    mentor.id,
                    mentees[8].token.accessToken,
                ),
                멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(mentees[9].id, mentor.token.accessToken),
            )

            멘티가_멘토의_커피챗_제안을_1차_수락한다(
                coffeeChats[0],
                LocalDateTime.of(2024, 2, 5, 18, 0),
                LocalDateTime.of(2024, 2, 5, 18, 30),
                mentees[0].token.accessToken,
            )
            멘티가_멘토의_커피챗_제안을_거절한다(coffeeChats[2], mentees[2].token.accessToken)
            멘토가_멘티의_커피챗_신청을_수락한다(coffeeChats[4], StrategyFixture.KAKAO_ID, mentor.token.accessToken)
            멘토가_멘티의_커피챗_신청을_수락한다(coffeeChats[8], StrategyFixture.KAKAO_ID, mentor.token.accessToken)
            멘티가_멘토의_커피챗_제안을_1차_수락한다(
                coffeeChats[5],
                LocalDateTime.of(2024, 3, 15, 18, 0),
                LocalDateTime.of(2024, 3, 15, 18, 30),
                mentees[5].token.accessToken,
            )
            멘토가_Pending_상태인_커피챗에_대해서_최종_수락을_한다(coffeeChats[5], StrategyFixture.KAKAO_ID, mentor.token.accessToken)
            멘티가_멘토의_커피챗_제안을_1차_수락한다(
                coffeeChats[6],
                LocalDateTime.of(2024, 4, 1, 18, 0),
                LocalDateTime.of(2024, 4, 1, 18, 30),
                mentees[6].token.accessToken,
            )
            멘토가_Pending_상태인_커피챗에_대해서_최종_취소를_한다(coffeeChats[6], mentor.token.accessToken)
            멘티가_멘토의_커피챗_제안을_1차_수락한다(
                coffeeChats[9],
                LocalDateTime.of(2024, 4, 10, 18, 0),
                LocalDateTime.of(2024, 4, 10, 18, 30),
                mentees[9].token.accessToken,
            )

            /* 2024년 1월 */
            val response1: ValidatableResponse = 멘토의_예약된_스케줄_정보를_조회한다(
                mentorId = mentor.id,
                year = 2024,
                month = 1,
                accessToken = mentees[0].token.accessToken,
            ).statusCode(OK.value())
            assertReservedScheduleMatch(
                response = response1,
                mentor = MENTOR_1,
                reservedStart = listOf(),
                reservedEnd = listOf(),
            )

            /* 2024년 2월 */
            val response2: ValidatableResponse = 멘토의_예약된_스케줄_정보를_조회한다(
                mentorId = mentor.id,
                year = 2024,
                month = 2,
                accessToken = mentees[0].token.accessToken,
            ).statusCode(OK.value())
            assertReservedScheduleMatch(
                response = response2,
                mentor = MENTOR_1,
                reservedStart = listOf(
                    LocalDateTime.of(2024, 2, 5, 18, 0, 0),
                    LocalDateTime.of(2024, 2, 19, 18, 0, 0),
                ),
                reservedEnd = listOf(
                    LocalDateTime.of(2024, 2, 5, 18, 30, 0),
                    LocalDateTime.of(2024, 2, 19, 18, 30, 0),
                ),
            )

            /* 2024년 3월 */
            val response3: ValidatableResponse = 멘토의_예약된_스케줄_정보를_조회한다(
                mentorId = mentor.id,
                year = 2024,
                month = 3,
                accessToken = mentees[0].token.accessToken,
            ).statusCode(OK.value())
            assertReservedScheduleMatch(
                response = response3,
                mentor = MENTOR_1,
                reservedStart = listOf(
                    LocalDateTime.of(2024, 3, 4, 18, 0, 0),
                    LocalDateTime.of(2024, 3, 15, 18, 0, 0),
                ),
                reservedEnd = listOf(
                    LocalDateTime.of(2024, 3, 4, 18, 30, 0),
                    LocalDateTime.of(2024, 3, 15, 18, 30, 0),
                ),
            )

            /* 2024년 4월 */
            val response4: ValidatableResponse = 멘토의_예약된_스케줄_정보를_조회한다(
                mentorId = mentor.id,
                year = 2024,
                month = 4,
                accessToken = mentees[0].token.accessToken,
            ).statusCode(OK.value())
            assertReservedScheduleMatch(
                response = response4,
                mentor = MENTOR_1,
                reservedStart = listOf(
                    LocalDateTime.of(2024, 4, 5, 18, 0, 0),
                    LocalDateTime.of(2024, 4, 10, 18, 0, 0),
                    LocalDateTime.of(2024, 4, 17, 18, 0, 0),
                ),
                reservedEnd = listOf(
                    LocalDateTime.of(2024, 4, 5, 18, 30, 0),
                    LocalDateTime.of(2024, 4, 10, 18, 30, 0),
                    LocalDateTime.of(2024, 4, 17, 18, 30, 0),
                ),
            )

            /* 2024년 5월 */
            val response5: ValidatableResponse = 멘토의_예약된_스케줄_정보를_조회한다(
                mentorId = mentor.id,
                year = 2024,
                month = 5,
                accessToken = mentees[0].token.accessToken,
            ).statusCode(OK.value())
            assertReservedScheduleMatch(
                response = response5,
                mentor = MENTOR_1,
                reservedStart = listOf(),
                reservedEnd = listOf(),
            )
        }
    }

    private fun assertReservedScheduleMatch(
        response: ValidatableResponse,
        mentor: MentorFixture,
        reservedStart: List<LocalDateTime>,
        reservedEnd: List<LocalDateTime>,
    ) {
        response
            .body("period.startDate", `is`(mentor.mentoringPeriod.startDate.toString()))
            .body("period.endDate", `is`(mentor.mentoringPeriod.endDate.toString()))
            .body("schedules.dayOfWeek", contains(*mentor.timelines.map { it.dayOfWeek.kor }.toTypedArray()))
            .body("schedules.start.hour", contains(*mentor.timelines.map { it.startTime.hour }.toTypedArray()))
            .body("schedules.start.minute", contains(*mentor.timelines.map { it.startTime.minute }.toTypedArray()))
            .body("schedules.end.hour", contains(*mentor.timelines.map { it.endTime.hour }.toTypedArray()))
            .body("schedules.end.minute", contains(*mentor.timelines.map { it.endTime.minute }.toTypedArray()))
            .body("timeUnit", `is`(MentoringPeriod.TimeUnit.HALF_HOUR.value))

        when (reservedStart.isEmpty()) {
            true -> response.body("reserved", empty<Int>())
            false -> response
                .body("reserved.start", contains(*reservedStart.map { it.format(ISO_LOCAL_DATE_TIME) }.toTypedArray()))
                .body("reserved.end", contains(*reservedEnd.map { it.format(ISO_LOCAL_DATE_TIME) }.toTypedArray()))
        }
    }
}
