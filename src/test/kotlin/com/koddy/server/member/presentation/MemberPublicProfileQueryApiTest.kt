package com.koddy.server.member.presentation

import com.koddy.server.common.ApiDocsTestKt
import com.koddy.server.common.docs.ARRAY
import com.koddy.server.common.docs.BOOLEAN
import com.koddy.server.common.docs.DocumentField
import com.koddy.server.common.docs.NUMBER
import com.koddy.server.common.docs.OBJECT
import com.koddy.server.common.docs.STRING
import com.koddy.server.member.application.usecase.GetMemberPublicProfileUseCase
import com.koddy.server.member.application.usecase.query.response.MenteePublicProfile
import com.koddy.server.member.application.usecase.query.response.MentorPublicProfile
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest

@WebMvcTest(MemberPublicProfileQueryApi::class)
@DisplayName("Member -> MemberPublicProfileQueryApi 테스트")
internal class MemberPublicProfileQueryApiTest : ApiDocsTestKt() {
    @MockkBean
    private lateinit var getMemberPublicProfileUseCase: GetMemberPublicProfileUseCase

    @Nested
    @DisplayName("멘토 기본(Public) 프로필 조회 API [GET /api/mentors/{mentorId}]")
    internal inner class GetMentorPublicProfile {
        private val baseUrl = "/api/mentors/{mentorId}"

        private val pathParameters: Array<DocumentField> = arrayOf(
            "mentorId" type NUMBER means "멘토 ID(PK)",
        )
        private val responseFields: Array<DocumentField> = arrayOf(
            "id" type NUMBER means "멘토 ID(PK)",
            "name" type STRING means "이름",
            "profileImageUrl" type STRING means "프로필 이미지 URL" constraint "Nullable",
            "introduction" type STRING means "자기소개" constraint "Nullable",
            "languages" type OBJECT means "사용 가능한 언어" constraint "KR EN CN JP VN",
            "languages.main" type STRING means "메인 언어" constraint "1개",
            "languages.sub[]" type ARRAY means "서브 언어" constraint "0..N개",
            "school" type STRING means "학교",
            "major" type STRING means "전공",
            "enteredIn" type NUMBER means "학번",
            "authenticated" type BOOLEAN means "대학 인증 여부",
        )

        @Test
        fun `멘토 기본(Public) 프로필 정보를 조회한다`() {
            val response = MentorPublicProfile.from(mentor)
            every { getMemberPublicProfileUseCase.getMentorProfile(any()) } returns response

            getRequest(baseUrl, arrayOf(1L)) {
            }.andExpect {
                status { isOk() }
                content { success(response) }
            }.andDo {
                makeSuccessDocs("MemberApi/PublicProfile/Mentor") {
                    pathParameters(*pathParameters)
                    responseFields(*responseFields)
                }
            }
        }
    }

    @Nested
    @DisplayName("멘티 기본(Public) 프로필 조회 API [GET /api/mentees/{menteeId}]")
    internal inner class GetMenteePublicProfile {
        private val baseUrl = "/api/mentees/{menteeId}"

        private val pathParameters: Array<DocumentField> = arrayOf(
            "menteeId" type NUMBER means "멘티 ID(PK)",
        )
        private val responseFields: Array<DocumentField> = arrayOf(
            "id" type NUMBER means "멘티 ID(PK)",
            "name" type STRING means "이름",
            "profileImageUrl" type STRING means "프로필 이미지 URL" constraint "Nullable",
            "nationality" type STRING means "국적" constraint "KR EN CN JP VN ETC",
            "introduction" type STRING means "자기소개" constraint "Nullable",
            "languages" type OBJECT means "사용 가능한 언어" constraint "KR EN CN JP VN",
            "languages.main" type STRING means "메인 언어" constraint "1개",
            "languages.sub[]" type ARRAY means "서브 언어" constraint "0..N개",
            "interestSchool" type STRING means "관심있는 학교",
            "interestMajor" type STRING means "관심있는 전공",
        )

        @Test
        fun `멘티 기본(Public) 프로필 정보를 조회한다`() {
            val response = MenteePublicProfile.from(mentee)
            every { getMemberPublicProfileUseCase.getMenteeProfile(any()) } returns response

            getRequest(baseUrl, arrayOf(1L)) {
            }.andExpect {
                status { isOk() }
                content { success(response) }
            }.andDo {
                makeSuccessDocs("MemberApi/PublicProfile/Mentee") {
                    pathParameters(*pathParameters)
                    responseFields(*responseFields)
                }
            }
        }
    }
}
