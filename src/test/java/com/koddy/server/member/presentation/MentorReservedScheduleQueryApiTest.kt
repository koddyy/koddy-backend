package com.koddy.server.member.presentation

import com.koddy.server.common.ApiDocsTestKt
import com.koddy.server.common.docs.ARRAY
import com.koddy.server.common.docs.DocumentField
import com.koddy.server.common.docs.NUMBER
import com.koddy.server.common.docs.OBJECT
import com.koddy.server.common.docs.STRING
import com.koddy.server.common.toLocalDate
import com.koddy.server.common.toLocalDateTime
import com.koddy.server.member.application.usecase.GetMentorReservedScheduleUseCase
import com.koddy.server.member.application.usecase.query.response.MentorReservedSchedule
import com.koddy.server.member.application.usecase.query.response.Reserved
import com.koddy.server.member.domain.model.mentor.MentoringPeriod
import com.koddy.server.member.domain.model.response.MentoringPeriodResponse
import com.koddy.server.member.domain.model.response.ScheduleResponse
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest

@WebMvcTest(MentorReservedScheduleQueryApi::class)
@DisplayName("Member -> MentorReservedScheduleQueryApi 테스트")
internal class MentorReservedScheduleQueryApiTest : ApiDocsTestKt() {
    @MockkBean
    private lateinit var getMentorReservedScheduleUseCase: GetMentorReservedScheduleUseCase

    @Nested
    @DisplayName("특정 Year-Month에 대해서 멘토의 예약된 스케줄 조회 API [GET /api/mentors/{mentorId}/reserved-schedule]")
    internal inner class GetMentorReservedSchedule {
        private val baseUrl = "/api/mentors/{mentorId}/reserved-schedule"

        private val pathParameters: Array<DocumentField> = arrayOf(
            "mentorId" type NUMBER means "멘토 ID(PK)",
        )
        private val queryParameters: Array<DocumentField> = arrayOf(
            "year" type NUMBER means "Year 정보",
            "month" type NUMBER means "Month 정보",
        )
        private val responseFields: Array<DocumentField> = arrayOf(
            "period" type OBJECT means "멘토링 기간" constraint "Nullable",
            "period.startDate" type STRING means "멘토링 기간 [시작 날짜]" constraint "[KST] yyyy-MM-dd",
            "period.endDate" type STRING means "멘토링 기간 [종료 날짜]" constraint "[KST] yyyy-MM-dd",
            "schedules" type ARRAY means "멘토링 주간 스케줄" constraint "0..N개",
            "schedules[].dayOfWeek" type STRING means "멘토링 주간 스케줄 [요일]" constraint "월 화 수 목 금 토 일",
            "schedules[].start.hour" type NUMBER means "멘토링 주간 스케줄 [시작 시간(Hour)]" constraint "0 ~ 23",
            "schedules[].start.minute" type NUMBER means "멘토링 주간 스케줄 [시작 시간(Minute)]" constraint "0 ~ 59",
            "schedules[].end.hour" type NUMBER means "멘토링 주간 스케줄 [종료 시간(Hour)]" constraint "0 ~ 23",
            "schedules[].end.minute" type NUMBER means "멘토링 주간 스케줄 [종료 시간(Minute)]" constraint "0 ~ 59",
            "timeUnit" type NUMBER means "멘토링 시간 단위" constraint "Minute 단위 [30, 60, ...], default = 30",
            "reserved" type ARRAY means "Year-Month에 예약된 시간" constraint "0..N개",
            "reserved[].start" type STRING means "예약된 시간 [시작]" constraint "[KST] yyyy-MM-dd",
            "reserved[].end" type STRING means "예약된 시간 [종료]" constraint "[KST] yyyy-MM-dd",
        )

        @Test
        fun `특정 Year-Month에 대해서 멘토의 예약된 스케줄을 조회한다`() {
            val response = MentorReservedSchedule(
                period = MentoringPeriodResponse(
                    startDate = "2024/01/01".toLocalDate(),
                    endDate = "2024/12/31".toLocalDate(),
                ),
                schedules = listOf(
                    ScheduleResponse(
                        dayOfWeek = "월",
                        start = ScheduleResponse.Start(18, 0),
                        end = ScheduleResponse.End(23, 0),
                    ),
                    ScheduleResponse(
                        dayOfWeek = "토",
                        start = ScheduleResponse.Start(13, 0),
                        end = ScheduleResponse.End(19, 0),
                    ),
                ),
                timeUnit = MentoringPeriod.TimeUnit.HALF_HOUR.value,
                reserved = listOf(
                    Reserved(
                        start = "2024/2/7-18:00".toLocalDateTime(),
                        end = "2024/2/7-18:30".toLocalDateTime(),
                    ),
                    Reserved(
                        start = "2024/2/12-18:00".toLocalDateTime(),
                        end = "2024/2/12-18:30".toLocalDateTime(),
                    ),
                ),
            )
            every { getMentorReservedScheduleUseCase.invoke(any()) } returns response

            getRequest(baseUrl, arrayOf(1L)) {
                accessToken(mentee)
                param("year", "2024")
                param("month", "2")
            }.andExpect {
                status { isOk() }
                content { success(response) }
            }.andDo {
                makeSuccessDocsWithAccessToken("MemberApi/MentorReservedSchedule") {
                    pathParameters(*pathParameters)
                    queryParameters(*queryParameters)
                    responseFields(*responseFields)
                }
            }
        }
    }
}
