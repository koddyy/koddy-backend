package com.koddy.server.member.presentation

import com.koddy.server.auth.exception.AuthExceptionCode
import com.koddy.server.common.ApiDocsTestKt
import com.koddy.server.common.docs.BOOLEAN
import com.koddy.server.common.docs.DocumentField
import com.koddy.server.common.docs.NUMBER
import com.koddy.server.common.docs.STRING
import com.koddy.server.global.query.PageResponse
import com.koddy.server.global.query.SliceResponse
import com.koddy.server.member.application.usecase.MenteeMainSearchUseCase
import com.koddy.server.member.application.usecase.query.response.MentorSimpleSearchProfile
import com.koddy.server.member.application.usecase.query.response.SuggestedCoffeeChatsByMentorResponse
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest

@WebMvcTest(MenteeMainSearchApi::class)
@DisplayName("Member -> MenteeMainSearchApi 테스트")
internal class MenteeMainSearchApiTest : ApiDocsTestKt() {
    @MockkBean
    private lateinit var menteeMainSearchUseCase: MenteeMainSearchUseCase

    @Nested
    @DisplayName("커피챗 제안한 멘토 조회 API [GET /api/mentors/suggested-coffeechats]")
    internal inner class GetSuggestedMentors {
        private val baseUrl = "/api/mentors/suggested-coffeechats"

        private val queryParameters: Array<DocumentField> = arrayOf(
            "limit" type NUMBER means "데이터 Limit 개수" constraint "default = 3" isOptional true,
        )
        private val responseFields: Array<DocumentField> = arrayOf(
            "result[].coffeeChatId" type NUMBER means "커피챗 ID(PK)",
            "result[].mentorId" type NUMBER means "멘토 ID(PK)",
            "result[].name" type STRING means "이름",
            "result[].profileImageUrl" type STRING means "프로필 이미지 URL" constraint "Nullable",
            "result[].school" type STRING means "학교",
            "result[].major" type STRING means "전공",
            "result[].enteredIn" type NUMBER means "학번",
            "totalCount" type NUMBER means "전체 데이터 개수",
            "hasNext" type BOOLEAN means "다음 페이지 존재 여부",
        )

        @Test
        fun `멘티가 아니면 권한이 없다`() {
            val exceptionCode = AuthExceptionCode.INVALID_PERMISSION

            getRequest(baseUrl) {
                accessToken(mentor)
            }.andExpect {
                status { isForbidden() }
                content { exception(exceptionCode) }
            }.andDo {
                makeFailureDocsWithAccessToken("MemberApi/Mentee/MainSearch/SuggestedMentors/Failure") {
                    queryParameters(*queryParameters)
                }
            }
        }

        @Test
        fun `멘티 자신에게 커피챗을 제안한 멘토들을 최신순 기준으로 조회한다`() {
            val response: PageResponse<List<SuggestedCoffeeChatsByMentorResponse>> = PageResponse(
                result = listOf(
                    SuggestedCoffeeChatsByMentorResponse(
                        coffeeChatId = 3,
                        mentorId = 3,
                        name = "멘토3",
                        profileImageUrl = "https://mentor3-url",
                        school = "서울대학교",
                        major = "컴퓨터공학부",
                        enteredIn = 19,
                    ),
                ),
                totalCount = 1,
                hasNext = false,
            )
            every { menteeMainSearchUseCase.getSuggestedMentors(any()) } returns response

            getRequest(baseUrl) {
                accessToken(mentee)
            }.andExpect {
                status { isOk() }
                content { success(response) }
            }.andDo {
                makeSuccessDocsWithAccessToken("MemberApi/Mentee/MainSearch/SuggestedMentors/Success") {
                    queryParameters(*queryParameters)
                    responseFields(*responseFields)
                }
            }
        }
    }

    @Nested
    @DisplayName("멘토 둘러보기 API [GET /api/mentors]")
    internal inner class LookAroundMentorsByCondition {
        private val baseUrl = "/api/mentors"

        private val queryParameters: Array<DocumentField> = arrayOf(
            "languages" type STRING means "언어" constraint """
                - 국가 코드 기반 (KR EN CN JP VN) $enter
                - 여러개면 콤마로 구분 ex) KR,EN
            """ isOptional true,
            "page" type NUMBER means "페이지" constraint "1부터 시작",
        )
        private val responseFields: Array<DocumentField> = arrayOf(
            "result[].id" type NUMBER means "멘토 ID(PK)",
            "result[].name" type STRING means "이름",
            "result[].profileImageUrl" type STRING means "프로필 이미지 URL" constraint "Nullable",
            "result[].school" type STRING means "학교",
            "result[].major" type STRING means "전공",
            "result[].enteredIn" type NUMBER means "학번",
            "hasNext" type BOOLEAN means "다음 스크롤 존재 여부",
        )

        @Test
        fun `멘토들을 조건에 따라 둘러본다`() {
            val response: SliceResponse<List<MentorSimpleSearchProfile>> = SliceResponse(
                result = listOf(
                    MentorSimpleSearchProfile(
                        id = 3,
                        name = "멘토3",
                        profileImageUrl = "https://mentor3-url",
                        school = "서울대학교",
                        major = "컴퓨터공학부",
                        enteredIn = 19,
                    ),
                ),
                hasNext = false,
            )
            every { menteeMainSearchUseCase.lookAroundMentorsByCondition(any()) } returns response

            getRequest(baseUrl) {
                param("languages", "EN,CN")
                param("page", "1")
            }.andExpect {
                status { isOk() }
                content { success(response) }
            }.andDo {
                makeSuccessDocs("MemberApi/Mentee/MainSearch/Mentors") {
                    queryParameters(*queryParameters)
                    responseFields(*responseFields)
                }
            }
        }
    }
}
