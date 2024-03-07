package com.koddy.server.member.presentation

import com.koddy.server.auth.exception.AuthExceptionCode
import com.koddy.server.common.ApiDocsTestKt
import com.koddy.server.common.docs.ARRAY
import com.koddy.server.common.docs.DocumentField
import com.koddy.server.common.docs.NUMBER
import com.koddy.server.common.docs.OBJECT
import com.koddy.server.common.docs.STRING
import com.koddy.server.common.toLocalDate
import com.koddy.server.member.application.usecase.UpdateMentorProfileUseCase
import com.koddy.server.member.domain.model.Language
import com.koddy.server.member.domain.model.mentor.DayOfWeek
import com.koddy.server.member.presentation.request.MentorScheduleRequest
import com.koddy.server.member.presentation.request.UpdateMentorBasicInfoRequest
import com.koddy.server.member.presentation.request.UpdateMentorScheduleRequest
import com.koddy.server.member.presentation.request.model.LanguageRequestModel
import com.koddy.server.member.presentation.request.model.MentoringPeriodRequestModel
import com.ninjasquad.springmockk.MockkBean
import io.mockk.justRun
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest

@WebMvcTest(UpdateMentorProfileApi::class)
@DisplayName("Member -> UpdateMentorProfileApi 테스트")
internal class UpdateMentorProfileApiTest : ApiDocsTestKt() {
    @MockkBean
    private lateinit var updateMentorProfileUseCase: UpdateMentorProfileUseCase

    @Nested
    @DisplayName("멘토 기본정보 수정 API [PATCH /api/mentors/me/basic-info]")
    internal inner class UpdateBasicInfo {
        private val baseUrl = "/api/mentors/me/basic-info"
        private val request = UpdateMentorBasicInfoRequest(
            name = mentor.name,
            profileImageUrl = mentor.profileImageUrl,
            introduction = mentor.introduction,
            languages = LanguageRequestModel(
                main = Language.Category.KR.code,
                sub = listOf(
                    Language.Category.EN.code,
                    Language.Category.JP.code,
                    Language.Category.CN.code,
                ),
            ),
            school = mentor.universityProfile.school,
            major = mentor.universityProfile.major,
            enteredIn = mentor.universityProfile.enteredIn,
        )

        private val requestFields: Array<DocumentField> = arrayOf(
            "name" type STRING means "이름",
            "profileImageUrl" type STRING means "프로필 이미지 URL" isOptional true,
            "introduction" type STRING means "자기소개" isOptional true,
            "languages" type OBJECT means "사용 가능한 언어" constraint "KR EN CN JP VN",
            "languages.main" type STRING means "메인 언어" constraint "1개",
            "languages.sub[]" type ARRAY means "서브 언어" isOptional true,
            "school" type STRING means "학교",
            "major" type STRING means "전공",
            "enteredIn" type NUMBER means "학번",
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
                makeFailureDocsWithAccessToken("MemberApi/Update/Mentor/BasicInfo/Failure") {
                    requestFields(*requestFields)
                }
            }
        }

        @Test
        fun `멘토 기본정보를 수정한다`() {
            justRun { updateMentorProfileUseCase.updateBasicInfo(any()) }

            patchRequest(baseUrl) {
                accessToken(mentor)
                bodyContent(request)
            }.andExpect {
                status { isNoContent() }
            }.andDo {
                makeSuccessDocsWithAccessToken("MemberApi/Update/Mentee/BasicInfo/Success") {
                    requestFields(*requestFields)
                }
            }
        }
    }

    @Nested
    @DisplayName("멘토 스케줄 수정 API [PATCH /api/mentors/me/schedules]")
    internal inner class UpdateSchedule {
        private val baseUrl = "/api/mentors/me/schedules"
        private val request = UpdateMentorScheduleRequest(
            period = MentoringPeriodRequestModel(
                startDate = "2024/01/01".toLocalDate(),
                endDate = "2024/05/01".toLocalDate(),
            ),
            schedules = listOf(
                MentorScheduleRequest(
                    dayOfWeek = DayOfWeek.MON.kor,
                    start = MentorScheduleRequest.Start(9, 0),
                    end = MentorScheduleRequest.End(17, 0),
                ),
                MentorScheduleRequest(
                    dayOfWeek = DayOfWeek.WED.kor,
                    start = MentorScheduleRequest.Start(13, 0),
                    end = MentorScheduleRequest.End(20, 0),
                ),
            ),
        )

        private val requestFields: Array<DocumentField> = arrayOf(
            "period" type OBJECT means "멘토링 기간" isOptional true,
            "period.startDate" type STRING means "멘토링 기간 [시작 날짜]" constraint "[KST] yyyy-MM-dd",
            "period.endDate" type STRING means "멘토링 기간 [종료 날짜]" constraint "[KST] yyyy-MM-dd",
            "schedules" type ARRAY means "멘토링 주간 스케줄" isOptional true,
            "schedules[].dayOfWeek" type STRING means "멘토링 주간 스케줄 [요일]",
            "schedules[].start.hour" type NUMBER means "멘토링 주간 스케줄 [시작 시간(Hour)]" constraint "0 ~ 23",
            "schedules[].start.minute" type NUMBER means "멘토링 주간 스케줄 [종료 시간(Minute)]" constraint "0 ~ 59",
            "schedules[].end.hour" type NUMBER means "멘토링 주간 스케줄 [시작 시간(Hour)]" constraint "0 ~ 23",
            "schedules[].end.minute" type NUMBER means "멘토링 주간 스케줄 [종료 시간(Minute)]" constraint "0 ~ 59",
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
                makeFailureDocsWithAccessToken("MemberApi/Update/Mentor/Schedule/Failure") {
                    requestFields(*requestFields)
                }
            }
        }

        @Test
        fun `멘토 스케줄을 수정한다`() {
            justRun { updateMentorProfileUseCase.updateSchedule(any()) }

            patchRequest(baseUrl) {
                accessToken(mentor)
                bodyContent(request)
            }.andExpect {
                status { isNoContent() }
            }.andDo {
                makeSuccessDocsWithAccessToken("MemberApi/Update/Mentor/Schedule/Success") {
                    requestFields(*requestFields)
                }
            }
        }
    }
}
