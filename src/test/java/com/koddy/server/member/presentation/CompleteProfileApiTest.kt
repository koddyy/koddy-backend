package com.koddy.server.member.presentation

import com.koddy.server.auth.exception.AuthExceptionCode
import com.koddy.server.common.ApiDocsTestKt
import com.koddy.server.common.docs.ARRAY
import com.koddy.server.common.docs.DocumentField
import com.koddy.server.common.docs.NUMBER
import com.koddy.server.common.docs.OBJECT
import com.koddy.server.common.docs.STRING
import com.koddy.server.member.application.usecase.CompleteProfileUseCase
import com.koddy.server.member.presentation.request.CompleteMenteeProfileRequest
import com.koddy.server.member.presentation.request.CompleteMentorProfileRequest
import com.koddy.server.member.presentation.request.MentorScheduleRequest
import com.koddy.server.member.presentation.request.model.MentoringPeriodRequestModel
import com.ninjasquad.springmockk.MockkBean
import io.mockk.justRun
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest

@WebMvcTest(CompleteProfileApi::class)
@DisplayName("Member -> CompleteProfileApi 테스트")
internal class CompleteProfileApiTest : ApiDocsTestKt() {
    @MockkBean
    private lateinit var completeProfileUseCase: CompleteProfileUseCase

    @Nested
    @DisplayName("멘토 프로필 완성 API [PATCH /api/mentors/me/complete]")
    internal inner class CompleteMentor {
        private val baseUrl = "/api/mentors/me/complete"
        private val request = CompleteMentorProfileRequest(
            introduction = mentor.introduction,
            profileImageUrl = mentor.profileImageUrl,
            period = MentoringPeriodRequestModel(startDate = mentor.mentoringPeriod?.startDate, endDate = mentor.mentoringPeriod?.endDate),
            schedules = mentor.schedules
                .map { it.timeline }
                .map {
                    MentorScheduleRequest(
                        dayOfWeek = it.dayOfWeek.kor,
                        start = MentorScheduleRequest.Start(hour = it.startTime.hour, minute = it.startTime.minute),
                        end = MentorScheduleRequest.End(hour = it.endTime.hour, minute = it.endTime.minute),
                    )
                },
        )

        private val requestFields: Array<DocumentField> = arrayOf(
            "introduction" type STRING means "자기소개" isOptional true,
            "profileImageUrl" type STRING means "프로필 이미지 URL" isOptional true,
            "period" type OBJECT means "멘토링 기간" isOptional true,
            "period.startDate" type STRING means "멘토링 기간 [시작 날짜]" constraint "[KST] yyyy-MM-dd",
            "period.endDate" type STRING means "멘토링 기간 [종료 날짜]" constraint "[KST] yyyy-MM-dd",
            "schedules" type ARRAY means "멘토링 주간 스케줄" isOptional true,
            "schedules[].dayOfWeek" type STRING means "요일" constraint "월 화 수 목 금 토 일",
            "schedules[].start.hour" type NUMBER means "요일별 시작 시간 [Hour]" constraint "0 ~ 23",
            "schedules[].start.minute" type NUMBER means "요일별 시작 시간 [Minute]" constraint "0 ~ 59",
            "schedules[].end.hour" type NUMBER means "요일별 종료 시간 [Hour]" constraint "0 ~ 23",
            "schedules[].end.minute" type NUMBER means "요일별 시작 시간 [Minute]" constraint "0 ~ 59",
        )

        @Test
        fun `멘토가 아니면 권한이 없다`() {
            val exceptionCode = AuthExceptionCode.INVALID_PERMISSION

            patchRequest(baseUrl) {
                accessToken(mentee)
                bodyContent(request)
            }.andExpect {
                status { isForbidden() }
                content { exception(exceptionCode) }
            }.andDo {
                makeFailureDocsWithAccessToken("MemberApi/Complete/Mentor/Failure") {
                    requestFields(*requestFields)
                }
            }
        }

        @Test
        fun `멘토 프로필을 완성한다`() {
            justRun { completeProfileUseCase.completeMentor(any()) }

            patchRequest(baseUrl) {
                accessToken(mentor)
                bodyContent(request)
            }.andExpect {
                status { isNoContent() }
            }.andDo {
                makeSuccessDocsWithAccessToken("MemberApi/Complete/Mentor/Success") {
                    requestFields(*requestFields)
                }
            }
        }
    }

    @Nested
    @DisplayName("멘티 프로필 완성 API [PATCH /api/mentees/me/complete]")
    internal inner class CompleteMentee {
        private val baseUrl = "/api/mentees/me/complete"
        private val request = CompleteMenteeProfileRequest(
            introduction = mentee.introduction,
            profileImageUrl = mentee.profileImageUrl,
        )

        private val requestFields: Array<DocumentField> = arrayOf(
            "introduction" type STRING means "자기소개" isOptional true,
            "profileImageUrl" type STRING means "프로필 이미지 URL" isOptional true,
        )

        @Test
        fun `멘티가 아니면 권한이 없다`() {
            val exceptionCode = AuthExceptionCode.INVALID_PERMISSION

            patchRequest(baseUrl) {
                accessToken(mentor)
                bodyContent(request)
            }.andExpect {
                status { isForbidden() }
                content { exception(exceptionCode) }
            }.andDo {
                makeFailureDocsWithAccessToken("MemberApi/Complete/Mentee/Failure") {
                    requestFields(*requestFields)
                }
            }
        }

        @Test
        fun `멘티 프로필을 완성한다`() {
            justRun { completeProfileUseCase.completeMentee(any()) }

            patchRequest(baseUrl) {
                accessToken(mentee)
                bodyContent(request)
            }.andExpect {
                status { isNoContent() }
            }.andDo {
                makeSuccessDocsWithAccessToken("MemberApi/Complete/Mentee/Success") {
                    requestFields(*requestFields)
                }
            }
        }
    }
}
