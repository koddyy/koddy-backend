package com.koddy.server.member.presentation

import com.koddy.server.auth.exception.AuthExceptionCode
import com.koddy.server.common.ApiDocsTestKt
import com.koddy.server.common.docs.ARRAY
import com.koddy.server.common.docs.BOOLEAN
import com.koddy.server.common.docs.DocumentField
import com.koddy.server.common.docs.NUMBER
import com.koddy.server.common.docs.OBJECT
import com.koddy.server.common.docs.STRING
import com.koddy.server.member.application.usecase.GetMemberPrivateProfileUseCase
import com.koddy.server.member.application.usecase.query.response.MenteePrivateProfile
import com.koddy.server.member.application.usecase.query.response.MentorPrivateProfile
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest

@WebMvcTest(MemberPrivateProfileQueryApi::class)
@DisplayName("Member -> MemberPrivateProfileQueryApi 테스트")
internal class MemberPrivateProfileQueryApiTest : ApiDocsTestKt() {
    @MockkBean
    private lateinit var getMemberPrivateProfileUseCase: GetMemberPrivateProfileUseCase

    private val mentorProfileResponseFields: Array<DocumentField> = arrayOf(
        "id" type NUMBER means "멘토 ID(PK)",
        "email" type STRING means "이메일",
        "name" type STRING means "이름",
        "profileImageUrl" type STRING means "프로필 이미지 URL" constraint "Nullable",
        "nationality" type STRING means "국적" constraint "KR EN CN JP VN ETC",
        "introduction" type STRING means "자기소개" constraint "Nullable",
        "languages" type OBJECT means "사용 가능한 언어" constraint "KR EN CN JP VN",
        "languages.main" type STRING means "메인 언어" constraint "1개",
        "languages.sub[]" type ARRAY means "서브 언어" constraint "0..N개",
        "school" type STRING means "학교",
        "major" type STRING means "전공",
        "enteredIn" type NUMBER means "학번",
        "authenticated" type BOOLEAN means "대학 인증 여부",
        "period" type OBJECT means "멘토링 기간" constraint "Nullable",
        "period.startDate" type STRING means "멘토링 기간 [시작 날짜]" constraint "[KST] yyyy-MM-dd",
        "period.endDate" type STRING means "멘토링 기간 [종료 날짜]" constraint "[KST] yyyy-MM-dd",
        "schedules" type ARRAY means "멘토링 주간 스케줄" constraint "0..N개",
        "schedules[].dayOfWeek" type STRING means "멘토링 주간 스케줄 [요일]" constraint "월 화 수 목 금 토 일",
        "schedules[].start.hour" type NUMBER means "멘토링 주간 스케줄 [시작 시간(Hour)]" constraint "0 ~ 23",
        "schedules[].start.minute" type NUMBER means "멘토링 주간 스케줄 [시작 시간(Minute)]" constraint "0 ~ 59",
        "schedules[].end.hour" type NUMBER means "멘토링 주간 스케줄 [종료 시간(Hour)]" constraint "0 ~ 23",
        "schedules[].end.minute" type NUMBER means "멘토링 주간 스케줄 [종료 시간(Minute)]" constraint "0 ~ 59",
        "role" type STRING means "역할",
        "profileComplete" type BOOLEAN means "프로필 완성 여부 (자기소개 & 프로필 이미지 URL & 멘토링 기간 & 멘토링 주간 스케줄)",
    )
    private val menteeProfileResponseFields: Array<DocumentField> = arrayOf(
        "id" type NUMBER means "멘티 ID(PK)",
        "email" type STRING means "이메일",
        "name" type STRING means "이름",
        "profileImageUrl" type STRING means "프로필 이미지 URL" constraint "Nullable",
        "nationality" type STRING means "국적" constraint "KR EN CN JP VN ETC",
        "introduction" type STRING means "자기소개" constraint "Nullable",
        "languages" type OBJECT means "사용 가능한 언어" constraint "KR EN CN JP VN",
        "languages.main" type STRING means "메인 언어" constraint "1개",
        "languages.sub[]" type ARRAY means "서브 언어" constraint "0..N개",
        "interestSchool" type STRING means "관심있는 학교",
        "interestMajor" type STRING means "관심있는 전공",
        "role" type STRING means "역할",
        "profileComplete" type BOOLEAN means "프로필 완성 여부 (자기소개 & 프로필 이미지 URL)",
    )

    @Nested
    @DisplayName("사용자 마이페이지(Private) 프로필 조회 API [GET /api/members/me]")
    internal inner class GetPrivateProfile {
        private val baseUrl = "/api/members/me"

        @Test
        fun `멘토 마이페이지(Private) 프로필을 조회한다`() {
            val response: MentorPrivateProfile = MentorPrivateProfile.from(mentor)
            every { getMemberPrivateProfileUseCase.getMentorProfile(any()) } returns response

            getRequest(baseUrl) {
                accessToken(mentor)
            }.andExpect {
                status { isOk() }
                content { success(response) }
            }.andDo {
                makeSuccessDocsWithAccessToken("MemberApi/PrivateProfile/Mix/Mentor") {
                    responseFields(*mentorProfileResponseFields)
                }
            }
        }

        @Test
        fun `멘티 마이페이지(Private) 프로필을 조회한다`() {
            val response: MenteePrivateProfile = MenteePrivateProfile.from(mentee)
            every { getMemberPrivateProfileUseCase.getMenteeProfile(any()) } returns response

            getRequest(baseUrl) {
                accessToken(mentee)
            }.andExpect {
                status { isOk() }
                content { success(response) }
            }.andDo {
                makeSuccessDocsWithAccessToken("MemberApi/PrivateProfile/Mix/Mentee") {
                    responseFields(*menteeProfileResponseFields)
                }
            }
        }
    }

    @Nested
    @DisplayName("멘토 마이페이지(Private) 프로필 조회 API [GET /api/mentors/me]")
    internal inner class GetMentorPrivateProfile {
        private val baseUrl = "/api/mentors/me"

        @Test
        fun `멘토가 아니면 권한이 없다`() {
            val exceptionCode = AuthExceptionCode.INVALID_PERMISSION

            getRequest(baseUrl) {
                accessToken(mentee)
            }.andExpect {
                status { isForbidden() }
                content { exception(exceptionCode) }
            }.andDo {
                makeFailureDocsWithAccessToken("MemberApi/PrivateProfile/Mentor/Failure") {}
            }
        }

        @Test
        fun `멘토 마이페이지(Private) 프로필 정보를 조회한다`() {
            val response: MentorPrivateProfile = MentorPrivateProfile.from(mentor)
            every { getMemberPrivateProfileUseCase.getMentorProfile(any()) } returns response

            getRequest(baseUrl) {
                accessToken(mentor)
            }.andExpect {
                status { isOk() }
                content { success(response) }
            }.andDo {
                makeSuccessDocsWithAccessToken("MemberApi/PrivateProfile/Mentor/Success") {
                    responseFields(*mentorProfileResponseFields)
                }
            }
        }
    }

    @Nested
    @DisplayName("멘티 마이페이지(Private) 프로필 조회 API [GET /api/mentees/me]")
    internal inner class GetMenteePrivateProfile {
        private val baseUrl = "/api/mentees/me"

        @Test
        fun `멘티가 아니면 권한이 없다`() {
            val exceptionCode = AuthExceptionCode.INVALID_PERMISSION

            getRequest(baseUrl) {
                accessToken(mentor)
            }.andExpect {
                status { isForbidden() }
                content { exception(exceptionCode) }
            }.andDo {
                makeFailureDocsWithAccessToken("MemberApi/PrivateProfile/Mentee/Failure") {}
            }
        }

        @Test
        fun `멘티 마이페이지(Private) 프로필 정보를 조회한다`() {
            val response: MenteePrivateProfile = MenteePrivateProfile.from(mentee)
            every { getMemberPrivateProfileUseCase.getMenteeProfile(any()) } returns response

            getRequest(baseUrl) {
                accessToken(mentee)
            }.andExpect {
                status { isOk() }
                content { success(response) }
            }.andDo {
                makeSuccessDocsWithAccessToken("MemberApi/PrivateProfile/Mix/Mentee") {
                    responseFields(*menteeProfileResponseFields)
                }
            }
        }
    }
}
