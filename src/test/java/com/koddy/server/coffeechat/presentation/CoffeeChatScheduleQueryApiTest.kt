package com.koddy.server.coffeechat.presentation

import com.koddy.server.coffeechat.application.usecase.GetCoffeeChatScheduleUseCase
import com.koddy.server.coffeechat.application.usecase.query.response.CoffeeChatEachCategoryCounts
import com.koddy.server.coffeechat.application.usecase.query.response.MenteeCoffeeChatSchedule
import com.koddy.server.coffeechat.application.usecase.query.response.MentorCoffeeChatSchedule
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus
import com.koddy.server.coffeechat.domain.model.response.CoffeeChatSimpleDetails
import com.koddy.server.coffeechat.domain.model.response.MenteeSimpleDetails
import com.koddy.server.coffeechat.domain.model.response.MentorSimpleDetails
import com.koddy.server.common.ApiDocsTestKt
import com.koddy.server.common.docs.BOOLEAN
import com.koddy.server.common.docs.DocumentField
import com.koddy.server.common.docs.NUMBER
import com.koddy.server.common.docs.STRING
import com.koddy.server.global.query.SliceResponse
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest

@WebMvcTest(CoffeeChatScheduleQueryApi::class)
@DisplayName("CoffeeChat -> CoffeeChatScheduleQueryApi 테스트")
internal class CoffeeChatScheduleQueryApiTest : ApiDocsTestKt() {
    @MockkBean
    private lateinit var getCoffeeChatScheduleUseCase: GetCoffeeChatScheduleUseCase

    @Nested
    @DisplayName("내 일정 상태별 커피챗 개수 조회 API [GET /api/coffeechats/me/category-counts]")
    internal inner class GetEachCategoryCounts {
        private val baseUrl = "/api/coffeechats/me/category-counts"

        private val responseFields: Array<DocumentField> = arrayOf(
            "waiting" type NUMBER means "대기 일정 개수",
            "suggested" type NUMBER means "제안 일정 개수",
            "scheduled" type NUMBER means "예정 일정 개수",
            "passed" type NUMBER means "지나간 일정 개수",
        )

        @Test
        fun `내 일정 상태별 커피챗 개수를 조회한다`() {
            val response = CoffeeChatEachCategoryCounts(
                waiting = 3L,
                suggested = 1L,
                scheduled = 0L,
                passed = 2L,
            )
            every { getCoffeeChatScheduleUseCase.getEachCategoryCounts(any()) } returns response

            getRequest(baseUrl) {
                accessToken(common)
            }.andExpect {
                status { isOk() }
                content { success(response) }
            }.andDo {
                makeSuccessDocsWithAccessToken("CoffeeChatApi/MySchedule/CategoryCounts") {
                    responseFields(*responseFields)
                }
            }
        }
    }

    @Nested
    @DisplayName("내 일정 상태별 커피챗 정보 조회 API [GET /api/coffeechats/me/schedules]")
    internal inner class GetSchedules {
        private val baseUrl = "/api/coffeechats/me/schedules"

        private val queryParameters: Array<DocumentField> = arrayOf(
            "category" type STRING means "카테고리 필터" constraint """
                 - 대기 = waiting $enter
                 - 제안 = suggested $enter
                 - 예정 = scheduled $enter
                 - 지나감 = passed
            """,
            "detail" type STRING means "상세 필터" constraint """
                [안보내면 전체] $enter
                - 신청(with 대기) = apply $enter
                - 수락(with 대기) = pending $enter
                - 예정(with 예정) = approve $enter
                - 취소(with 지나감) = cancel $enter
                - 최종 취소(with 지나감) = cancel $enter
                - 자동 취소(with 지나감) = cancel $enter
                - 거절(with 지나감) = reject $enter
                - 완료(with 지나감) = complete
            """ isOptional true,
            "page" type NUMBER means "페이지" constraint "1부터 시작",
        )
        private val mentorResponseFields: Array<DocumentField> = arrayOf(
            "result[].coffeeChat.id" type NUMBER means "커피챗 ID(PK)",
            "result[].coffeeChat.status" type STRING means "커피챗 상태",
            "result[].mentee.id" type NUMBER means "멘티 ID(PK)",
            "result[].mentee.name" type STRING means "이름",
            "result[].mentee.profileImageUrl" type STRING means "프로필 이미지 URL" constraint "Nullable",
            "result[].mentee.interestSchool" type STRING means "관심있는 학교",
            "result[].mentee.interestMajor" type STRING means "관심있는 전공",
            "hasNext" type BOOLEAN means "다음 스크롤 존재 여부",
        )
        private val menteeResponseFields: Array<DocumentField> = arrayOf(
            "result[].coffeeChat.id" type NUMBER means "커피챗 ID(PK)",
            "result[].coffeeChat.status" type STRING means "커피챗 상태",
            "result[].mentor.id" type NUMBER means "멘토 ID(PK)",
            "result[].mentor.name" type STRING means "이름",
            "result[].mentor.profileImageUrl" type STRING means "프로필 이미지 URL" constraint "Nullable",
            "result[].mentor.school" type STRING means "학교",
            "result[].mentor.major" type STRING means "전공",
            "result[].mentor.enteredIn" type NUMBER means "학번",
            "hasNext" type BOOLEAN means "다음 스크롤 존재 여부",
        )

        @Test
        fun `멘토의 내 일정에서 상태별 커피챗 정보를 조회한다`() {
            val response = SliceResponse(
                result = listOf(
                    MentorCoffeeChatSchedule(
                        coffeeChat = CoffeeChatSimpleDetails(
                            id = 1L,
                            status = CoffeeChatStatus.MENTEE_APPLY.name,
                        ),
                        mentee = MenteeSimpleDetails(
                            id = mentee.id,
                            name = mentee.name,
                            profileImageUrl = mentee.profileImageUrl,
                            interestSchool = mentee.interest.school,
                            interestMajor = mentee.interest.major,
                        ),
                    ),
                ),
                hasNext = false,
            )
            every { getCoffeeChatScheduleUseCase.getMentorSchedules(any()) } returns response

            getRequest(baseUrl) {
                accessToken(mentor)
                param("category", "waiting")
                param("detail", "apply")
                param("page", "1")
            }.andExpect {
                status { isOk() }
                content { success(response) }
            }.andDo {
                makeSuccessDocsWithAccessToken("CoffeeChatApi/MySchedule/Mentor") {
                    queryParameters(*queryParameters)
                    responseFields(*mentorResponseFields)
                }
            }
        }

        @Test
        fun `멘티의 내 일정에서 상태별 커피챗 정보를 조회한다`() {
            val response = SliceResponse(
                result = listOf(
                    MenteeCoffeeChatSchedule(
                        coffeeChat = CoffeeChatSimpleDetails(
                            id = 1L,
                            status = CoffeeChatStatus.MENTEE_APPLY.name,
                        ),
                        mentor = MentorSimpleDetails(
                            id = mentor.id,
                            name = mentor.name,
                            profileImageUrl = mentor.profileImageUrl,
                            school = mentor.universityProfile.school,
                            major = mentor.universityProfile.major,
                            enteredIn = mentor.universityProfile.enteredIn,
                        ),
                    ),
                ),
                hasNext = false,
            )
            every { getCoffeeChatScheduleUseCase.getMenteeSchedules(any()) } returns response

            getRequest(baseUrl) {
                accessToken(mentee)
                param("category", "waiting")
                param("detail", "apply")
                param("page", "1")
            }.andExpect {
                status { isOk() }
                content { success(response) }
            }.andDo {
                makeSuccessDocsWithAccessToken("CoffeeChatApi/MySchedule/Mentee") {
                    queryParameters(*queryParameters)
                    responseFields(*menteeResponseFields)
                }
            }
        }
    }
}
