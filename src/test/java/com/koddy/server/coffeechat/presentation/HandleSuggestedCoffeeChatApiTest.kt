package com.koddy.server.coffeechat.presentation

import com.koddy.server.auth.exception.AuthExceptionCode
import com.koddy.server.coffeechat.application.usecase.HandleSuggestedCoffeeChatUseCase
import com.koddy.server.coffeechat.presentation.request.PendingSuggestedCoffeeChatRequest
import com.koddy.server.coffeechat.presentation.request.RejectSuggestedCoffeeChatRequest
import com.koddy.server.common.ApiDocsTestKt
import com.koddy.server.common.docs.DocumentField
import com.koddy.server.common.docs.NUMBER
import com.koddy.server.common.docs.STRING
import com.koddy.server.common.toLocalDateTime
import com.koddy.server.member.exception.MemberException
import com.koddy.server.member.exception.MemberExceptionCode
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.justRun
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest

@WebMvcTest(HandleSuggestedCoffeeChatApi::class)
@DisplayName("CoffeeChat -> HandleSuggestedCoffeeChatApi 테스트")
internal class HandleSuggestedCoffeeChatApiTest : ApiDocsTestKt() {
    @MockkBean
    private lateinit var handleSuggestedCoffeeChatUseCase: HandleSuggestedCoffeeChatUseCase

    @Nested
    @DisplayName("멘토가 제안한 커피챗 거절 API [PATCH /api/coffeechats/suggested/reject/{coffeeChatId}]")
    internal inner class Reject {
        private val baseUrl = "/api/coffeechats/suggested/reject/{coffeeChatId}"
        private val request = RejectSuggestedCoffeeChatRequest(rejectReason = "거절..")

        private val pathParameters: Array<DocumentField> = arrayOf(
            "coffeeChatId" type NUMBER means "커피챗 ID(PK)",
        )
        private val requestFields: Array<DocumentField> = arrayOf(
            "rejectReason" type STRING means "거절 사유",
        )

        @Test
        fun `멘티가 아니면 권한이 없다`() {
            val exceptionCode = AuthExceptionCode.INVALID_PERMISSION

            patchRequest(baseUrl, arrayOf(1L)) {
                accessToken(mentor)
                bodyContent(request)
            }.andExpect {
                status { isForbidden() }
                content { exception(exceptionCode) }
            }.andDo {
                makeFailureDocsWithAccessToken("CoffeeChatApi/LifeCycle/SuggestedByMentor/Reject/Failure") {
                    pathParameters(*pathParameters)
                    requestFields(*requestFields)
                }
            }
        }

        @Test
        fun `멘티는 멘토가 제안한 커피챗을 거절한다`() {
            justRun { handleSuggestedCoffeeChatUseCase.reject(any()) }

            patchRequest(baseUrl, arrayOf(1L)) {
                accessToken(mentee)
                bodyContent(request)
            }.andExpect {
                status { isNoContent() }
            }.andDo {
                makeSuccessDocsWithAccessToken("CoffeeChatApi/LifeCycle/SuggestedByMentor/Reject/Success") {
                    pathParameters(*pathParameters)
                    requestFields(*requestFields)
                }
            }
        }
    }

    @Nested
    @DisplayName("멘토가 제안한 커피챗 1차 수락 API [PATCH /api/coffeechats/suggested/pending/{coffeeChatId}]")
    internal inner class Pending {
        private val baseUrl = "/api/coffeechats/suggested/pending/{coffeeChatId}"
        private val request = PendingSuggestedCoffeeChatRequest(
            question = "궁금한 점..",
            start = "2024/3/1-18:00".toLocalDateTime().toString(),
            end = "2024/3/1-18:30".toLocalDateTime().toString(),
        )

        private val pathParameters: Array<DocumentField> = arrayOf(
            "coffeeChatId" type NUMBER means "커피챗 ID(PK)",
        )
        private val requestFields: Array<DocumentField> = arrayOf(
            "question" type STRING means "멘토에게 궁금한 점",
            "start" type STRING means "커피챗 날짜 (시작)" constraint "[KST] yyyy-MM-ddTHH:mm:ss $enter -> 시간 = 00:00:00 ~ 23:59:59",
            "end" type STRING means "커피챗 날짜 (종료)" constraint "[KST] yyyy-MM-ddTHH:mm:ss $enter -> 시간 = 00:00:00 ~ 23:59:59",
        )

        @Test
        fun `멘티가 아니면 권한이 없다`() {
            val exceptionCode = AuthExceptionCode.INVALID_PERMISSION

            patchRequest(baseUrl, arrayOf(1L)) {
                accessToken(mentor)
                bodyContent(request)
            }.andExpect {
                status { isForbidden() }
                content { exception(exceptionCode) }
            }.andDo {
                makeFailureDocsWithAccessToken("CoffeeChatApi/LifeCycle/SuggestedByMentor/Pending/Failure/Case1") {
                    pathParameters(*pathParameters)
                    requestFields(*requestFields)
                }
            }
        }

        @Test
        fun `멘토가 멘토링 관련 정보를 기입하지 않았으면 예약 날짜를 선택할 수 없다`() {
            val exceptionCode = MemberExceptionCode.MENTOR_NOT_FILL_IN_SCHEDULE
            every { handleSuggestedCoffeeChatUseCase.pending(any()) } throws MemberException(exceptionCode)

            patchRequest(baseUrl, arrayOf(1L)) {
                accessToken(mentee)
                bodyContent(request)
            }.andExpect {
                status { isConflict() }
                content { exception(exceptionCode) }
            }.andDo {
                makeFailureDocsWithAccessToken("CoffeeChatApi/LifeCycle/SuggestedByMentor/Pending/Failure/Case2") {
                    pathParameters(*pathParameters)
                    requestFields(*requestFields)
                }
            }
        }

        @Test
        fun `이미 예약되었거나 멘토링이 가능하지 않은 날짜면 예외가 발생한다`() {
            val exceptionCode = MemberExceptionCode.CANNOT_RESERVATION
            every { handleSuggestedCoffeeChatUseCase.pending(any()) } throws MemberException(exceptionCode)

            patchRequest(baseUrl, arrayOf(1L)) {
                accessToken(mentee)
                bodyContent(request)
            }.andExpect {
                status { isConflict() }
                content { exception(exceptionCode) }
            }.andDo {
                makeFailureDocsWithAccessToken("CoffeeChatApi/LifeCycle/SuggestedByMentor/Pending/Failure/Case3") {
                    pathParameters(*pathParameters)
                    requestFields(*requestFields)
                }
            }
        }

        @Test
        fun `멘티는 멘토가 제안한 커피챗을 1차 수락한다`() {
            justRun { handleSuggestedCoffeeChatUseCase.pending(any()) }

            patchRequest(baseUrl, arrayOf(1L)) {
                accessToken(mentee)
                bodyContent(request)
            }.andExpect {
                status { isNoContent() }
            }.andDo {
                makeSuccessDocsWithAccessToken("CoffeeChatApi/LifeCycle/SuggestedByMentor/Pending/Success") {
                    pathParameters(*pathParameters)
                    requestFields(*requestFields)
                }
            }
        }
    }
}
