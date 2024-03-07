package com.koddy.server.coffeechat.presentation

import com.koddy.server.auth.exception.AuthExceptionCode
import com.koddy.server.coffeechat.application.usecase.CreateCoffeeChatUseCase
import com.koddy.server.coffeechat.presentation.request.CreateCoffeeChatByApplyRequest
import com.koddy.server.coffeechat.presentation.request.CreateCoffeeChatBySuggestRequest
import com.koddy.server.common.ApiDocsTestKt
import com.koddy.server.common.docs.DocumentField
import com.koddy.server.common.docs.NUMBER
import com.koddy.server.common.docs.STRING
import com.koddy.server.common.toLocalDateTime
import com.koddy.server.global.ResponseWrapper
import com.koddy.server.member.exception.MemberException
import com.koddy.server.member.exception.MemberExceptionCode
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest

@WebMvcTest(CreateCoffeeChatApi::class)
@DisplayName("CoffeeChat -> CreateCoffeeChatApi 테스트")
internal class CreateCoffeeChatApiTest : ApiDocsTestKt() {
    @MockkBean
    private lateinit var createCoffeeChatUseCase: CreateCoffeeChatUseCase

    @Nested
    @DisplayName("멘티 -> 멘토 커피챗 신청 API [POST /api/coffeechats/apply]")
    internal inner class CreateCoffeeChatByApply {
        private val baseUrl = "/api/coffeechats/apply"
        private val request = CreateCoffeeChatByApplyRequest(
            mentorId = mentor.id,
            applyReason = "신청 이유..",
            start = "2024/3/1-18:00".toLocalDateTime().toString(),
            end = "2024/3/1-18:30".toLocalDateTime().toString(),
        )

        private val requestFields: Array<DocumentField> = arrayOf(
            "mentorId" type NUMBER means "멘토 ID(PK)",
            "applyReason" type STRING means "커피챗 신청 이유",
            "start" type STRING means "커피챗 날짜 (시작)" constraint "[KST] yyyy-MM-ddTHH:mm:ss $enter -> 시간 = 00:00:00 ~ 23:59:59",
            "end" type STRING means "커피챗 날짜 (종료)" constraint "[KST] yyyy-MM-ddTHH:mm:ss $enter -> 시간 = 00:00:00 ~ 23:59:59",
        )
        private val responseFields: Array<DocumentField> = arrayOf(
            "result" type NUMBER means "커피챗 ID(PK)",
        )

        @Test
        fun `멘티가 아니면 권한이 없다`() {
            val exceptionCode = AuthExceptionCode.INVALID_PERMISSION

            postRequest(baseUrl) {
                accessToken(mentor)
                bodyContent(request)
            }.andExpect {
                status { isForbidden() }
                content { exception(exceptionCode) }
            }.andDo {
                makeFailureDocsWithAccessToken("CoffeeChatApi/LifeCycle/Create/MenteeApply/Failure/Case1") {
                    requestFields(*requestFields)
                }
            }
        }

        @Test
        fun `멘토가 멘토링 관련 정보를 기입하지 않았으면 예약 날짜를 선택할 수 없다`() {
            val exceptionCode = MemberExceptionCode.MENTOR_NOT_FILL_IN_SCHEDULE
            every { createCoffeeChatUseCase.createByApply(any()) } throws MemberException(exceptionCode)

            postRequest(baseUrl) {
                accessToken(mentee)
                bodyContent(request)
            }.andExpect {
                status { isConflict() }
                content { exception(exceptionCode) }
            }.andDo {
                makeFailureDocsWithAccessToken("CoffeeChatApi/LifeCycle/Create/MenteeApply/Failure/Case2") {
                    requestFields(*requestFields)
                }
            }
        }

        @Test
        fun `이미 예약되었거나 멘토링이 가능하지 않은 날짜면 예외가 발생한다`() {
            val exceptionCode = MemberExceptionCode.CANNOT_RESERVATION
            every { createCoffeeChatUseCase.createByApply(any()) } throws MemberException(exceptionCode)

            postRequest(baseUrl) {
                accessToken(mentee)
                bodyContent(request)
            }.andExpect {
                status { isConflict() }
                content { exception(exceptionCode) }
            }.andDo {
                makeFailureDocsWithAccessToken("CoffeeChatApi/LifeCycle/Create/MenteeApply/Failure/Case3") {
                    requestFields(*requestFields)
                }
            }
        }

        @Test
        fun `멘티가 멘토에게 커피챗을 신청한다`() {
            val response = 1L
            every { createCoffeeChatUseCase.createByApply(any()) } returns response

            postRequest(baseUrl) {
                accessToken(mentee)
                bodyContent(request)
            }.andExpect {
                status { isOk() }
                content { success(ResponseWrapper(response)) }
            }.andDo {
                makeSuccessDocsWithAccessToken("CoffeeChatApi/LifeCycle/Create/MenteeApply/Success") {
                    requestFields(*requestFields)
                    responseFields(*responseFields)
                }
            }
        }
    }

    @Nested
    @DisplayName("멘토 -> 멘티 커피챗 제안 API [POST /api/coffeechats/suggest]")
    internal inner class CreateCoffeeChatBySuggest {
        private val baseUrl = "/api/coffeechats/suggest"
        private val request = CreateCoffeeChatBySuggestRequest(
            menteeId = mentee.id,
            suggestReason = "제안 이유..",
        )

        private val requestFields: Array<DocumentField> = arrayOf(
            "menteeId" type NUMBER means "멘티 ID(PK)",
            "suggestReason" type STRING means "커피챗 제안 이유",
        )
        private val responseFields: Array<DocumentField> = arrayOf(
            "result" type NUMBER means "커피챗 ID(PK)",
        )

        @Test
        fun `멘토가 아니면 권한이 없다`() {
            val exceptionCode = AuthExceptionCode.INVALID_PERMISSION

            postRequest(baseUrl) {
                accessToken(mentee)
                bodyContent(request)
            }.andExpect {
                status { isForbidden() }
                content { exception(exceptionCode) }
            }.andDo {
                makeFailureDocsWithAccessToken("CoffeeChatApi/LifeCycle/Create/MentorSuggest/Failure") {
                    requestFields(*requestFields)
                }
            }
        }

        @Test
        fun `멘토가 멘티에게 커피챗을 제안한다`() {
            val response = 1L
            every { createCoffeeChatUseCase.createBySuggest(any()) } returns response

            postRequest(baseUrl) {
                accessToken(mentor)
                bodyContent(request)
            }.andExpect {
                status { isOk() }
                content { success(ResponseWrapper(response)) }
            }.andDo {
                makeSuccessDocsWithAccessToken("CoffeeChatApi/LifeCycle/Create/MentorSuggest/Success") {
                    requestFields(*requestFields)
                    responseFields(*responseFields)
                }
            }
        }
    }
}
