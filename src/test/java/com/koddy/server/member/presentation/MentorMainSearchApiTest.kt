package com.koddy.server.member.presentation

import com.koddy.server.auth.exception.AuthExceptionCode
import com.koddy.server.common.ApiDocsTestKt
import com.koddy.server.common.docs.BOOLEAN
import com.koddy.server.common.docs.DocumentField
import com.koddy.server.common.docs.NUMBER
import com.koddy.server.common.docs.STRING
import com.koddy.server.global.query.PageResponse
import com.koddy.server.global.query.SliceResponse
import com.koddy.server.member.application.usecase.MentorMainSearchUseCase
import com.koddy.server.member.application.usecase.query.response.AppliedCoffeeChatsByMenteeResponse
import com.koddy.server.member.application.usecase.query.response.MenteeSimpleSearchProfile
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest

@WebMvcTest(MentorMainSearchApi::class)
@DisplayName("Member -> MentorMainSearchApi 테스트")
internal class MentorMainSearchApiTest : ApiDocsTestKt() {
    @MockkBean
    private lateinit var mentorMainSearchUseCase: MentorMainSearchUseCase

    @Nested
    @DisplayName("커피챗 신청한 멘티 조회 API [GET /api/mentees/applied-coffeechats]")
    internal inner class GetAppliedMentees {
        private val baseUrl = "/api/mentees/applied-coffeechats"

        private val queryParameters: Array<DocumentField> = arrayOf(
            "limit" type NUMBER means "데이터 Limit 개수" constraint "default = 3" isOptional true,
        )
        private val responseFields: Array<DocumentField> = arrayOf(
            "result[].coffeeChatId" type NUMBER means "커피챗 ID(PK)",
            "result[].menteeId" type NUMBER means "멘티 ID(PK)",
            "result[].name" type STRING means "이름",
            "result[].profileImageUrl" type STRING means "프로필 이미지 URL" constraint "Nullable",
            "result[].nationality" type STRING means "국적" constraint "KR EN CN JP VN ETC",
            "result[].interestSchool" type STRING means "관심있는 학교",
            "result[].interestMajor" type STRING means "관심있는 전공",
            "totalCount" type NUMBER means "전체 데이터 개수",
            "hasNext" type BOOLEAN means "다음 페이지 존재 여부",
        )

        @Test
        fun `멘토가 아니면 권한이 없다`() {
            val exceptionCode = AuthExceptionCode.INVALID_PERMISSION

            getRequest(baseUrl) {
                accessToken(mentee)
            }.andExpect {
                status { isForbidden() }
                content { exception(exceptionCode) }
            }.andDo {
                makeFailureDocsWithAccessToken("MemberApi/Mentor/MainSearch/AppliedMentees/Failure") {
                    queryParameters(*queryParameters)
                }
            }
        }

        @Test
        fun `멘토 자신에게 커피챗을 신청한 멘티들을 최신순 기준으로 조회한다`() {
            val response: PageResponse<List<AppliedCoffeeChatsByMenteeResponse>> = PageResponse(
                result = listOf(
                    AppliedCoffeeChatsByMenteeResponse(
                        coffeeChatId = 3,
                        menteeId = 3,
                        name = "멘티3",
                        profileImageUrl = "https://mentee3-url",
                        nationality = "EN",
                        interestSchool = "서울대학교",
                        interestMajor = "컴퓨터공학부",
                    ),
                ),
                totalCount = 1,
                hasNext = false,
            )
            every { mentorMainSearchUseCase.getAppliedMentees(any()) } returns response

            getRequest(baseUrl) {
                accessToken(mentor)
            }.andExpect {
                status { isOk() }
                content { success(response) }
            }.andDo {
                makeSuccessDocsWithAccessToken("MemberApi/Mentor/MainSearch/AppliedMentees/Success") {
                    queryParameters(*queryParameters)
                    responseFields(*responseFields)
                }
            }
        }
    }

    @Nested
    @DisplayName("멘티 둘러보기 API [GET /api/mentees]")
    internal inner class LookAroundMenteesByCondition {
        private val baseUrl = "/api/mentees"

        private val queryParameters: Array<DocumentField> = arrayOf(
            "nationalities" type STRING means "국적" constraint "국가 코드 기반 (KR EN CN JP VN ETC) + 여러개면 콤마로 구분" isOptional true,
            "languages" type STRING means "언어" constraint "국가 코드 기반 (KR EN CN JP VN) + 여러개면 콤마로 구분" isOptional true,
            "page" type NUMBER means "페이지" constraint "1부터 시작",
        )
        private val responseFields: Array<DocumentField> = arrayOf(
            "result[].id" type NUMBER means "멘티 ID(PK)",
            "result[].name" type STRING means "이름",
            "result[].profileImageUrl" type STRING means "프로필 이미지 URL" constraint "Nullable",
            "result[].nationality" type STRING means "국적" constraint "KR EN CN JP VN ETC",
            "result[].interestSchool" type STRING means "관심있는 학교",
            "result[].interestMajor" type STRING means "관심있는 전공",
            "hasNext" type BOOLEAN means "다음 스크롤 존재 여부",
        )

        @Test
        fun `멘티들을 조건에 따라 둘러본다`() {
            val response: SliceResponse<List<MenteeSimpleSearchProfile>> = SliceResponse(
                result = listOf(
                    MenteeSimpleSearchProfile(
                        id = 3,
                        name = "멘티3",
                        profileImageUrl = "https://mentee3-url",
                        nationality = "EN",
                        interestSchool = "서울대학교",
                        interestMajor = "컴퓨터공학부",
                    ),
                ),
                hasNext = false,
            )
            every { mentorMainSearchUseCase.lookAroundMenteesByCondition(any()) } returns response

            getRequest(baseUrl) {
                accessToken(mentor)
                param("nationalities", "EN,JP,CN")
                param("languages", "EN,CN")
                param("page", "1")
            }.andExpect {
                status { isOk() }
                content { success(response) }
            }.andDo {
                makeSuccessDocsWithAccessToken("MemberApi/Mentor/MainSearch/Mentees") {
                    queryParameters(*queryParameters)
                    responseFields(*responseFields)
                }
            }
        }
    }
}
