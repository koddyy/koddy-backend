package com.koddy.server.coffeechat.presentation

import com.koddy.server.auth.exception.AuthExceptionCode
import com.koddy.server.coffeechat.application.usecase.HandlePendingCoffeeChatUseCase
import com.koddy.server.coffeechat.domain.model.Strategy
import com.koddy.server.coffeechat.presentation.request.FinallyApprovePendingCoffeeChatRequest
import com.koddy.server.coffeechat.presentation.request.FinallyCancelPendingCoffeeChatRequest
import com.koddy.server.common.ApiDocsTestKt
import com.koddy.server.common.docs.DocumentField
import com.koddy.server.common.docs.NUMBER
import com.koddy.server.common.docs.STRING
import com.ninjasquad.springmockk.MockkBean
import io.mockk.justRun
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest

@WebMvcTest(HandlePendingCoffeeChatApi::class)
@DisplayName("CoffeeChat -> HandlePendingCoffeeChatApi 테스트")
internal class HandlePendingCoffeeChatApiTest : ApiDocsTestKt() {
    @MockkBean
    private lateinit var handlePendingCoffeeChatUseCase: HandlePendingCoffeeChatUseCase

    @Nested
    @DisplayName("Pending 상태인 커피챗에 대한 최종 취소 API [PATCH /api/coffeechats/pending/cancel/{coffeeChatId}]")
    internal inner class FinallyCancel {
        private val baseUrl = "/api/coffeechats/pending/cancel/{coffeeChatId}"
        private val request = FinallyCancelPendingCoffeeChatRequest(cancelReason = "최종 취소..")

        private val pathParameters: Array<DocumentField> = arrayOf(
            "coffeeChatId" type NUMBER means "커피챗 ID(PK)",
        )
        private val requestFields: Array<DocumentField> = arrayOf(
            "cancelReason" type STRING means "최종 취소 사유",
        )

        @Test
        fun `멘토가 아니면 권한이 없다`() {
            val exceptionCode = AuthExceptionCode.INVALID_PERMISSION

            patchRequest(baseUrl, arrayOf(1L)) {
                accessToken(mentee)
                bodyContent(request)
            }.andExpect {
                status { isForbidden() }
                content { exception(exceptionCode) }
            }.andDo {
                makeFailureDocsWithAccessToken("CoffeeChatApi/LifeCycle/PendingCoffeeChat/Cancel/Failure") {
                    pathParameters(*pathParameters)
                    requestFields(*requestFields)
                }
            }
        }

        @Test
        fun `멘토는 Pending 상태인 커피챗에 대해서 최종 취소한다`() {
            justRun { handlePendingCoffeeChatUseCase.finallyCancel(any()) }

            patchRequest(baseUrl, arrayOf(1L)) {
                accessToken(mentor)
                bodyContent(request)
            }.andExpect {
                status { isNoContent() }
            }.andDo {
                makeSuccessDocsWithAccessToken("CoffeeChatApi/LifeCycle/PendingCoffeeChat/Cancel/Success") {
                    pathParameters(*pathParameters)
                    requestFields(*requestFields)
                }
            }
        }
    }

    @Nested
    @DisplayName("Pending 상태인 커피챗에 대한 최종 수락 API [PATCH /api/coffeechats/pending/approve/{coffeeChatId}]")
    internal inner class FinallyApprove {
        private val baseUrl = "/api/coffeechats/pending/approve/{coffeeChatId}"
        private val request = FinallyApprovePendingCoffeeChatRequest(
            chatType = Strategy.Type.KAKAO_ID.value,
            chatValue = "sjiwon",
        )

        private val pathParameters: Array<DocumentField> = arrayOf(
            "coffeeChatId" type NUMBER means "커피챗 ID(PK)",
        )
        private val requestFields: Array<DocumentField> = arrayOf(
            "chatType" type STRING means "멘토링 진행 방식" constraint "- 링크 = zoom, google $enter - 메신저 = kakao, line, wechat",
            "chatValue" type STRING means "멘토링 진행 방식에 대한 값" constraint "링크 or 메신저 ID",
        )

        @Test
        fun `멘토가 아니면 권한이 없다`() {
            val exceptionCode = AuthExceptionCode.INVALID_PERMISSION

            patchRequest(baseUrl, arrayOf(1L)) {
                accessToken(mentee)
                bodyContent(request)
            }.andExpect {
                status { isForbidden() }
                content { exception(exceptionCode) }
            }.andDo {
                makeFailureDocsWithAccessToken("CoffeeChatApi/LifeCycle/PendingCoffeeChat/Approve/Failure") {
                    pathParameters(*pathParameters)
                    requestFields(*requestFields)
                }
            }
        }

        @Test
        fun `멘토는 Pending 상태인 커피챗에 대해서 최종 수락한다`() {
            justRun { handlePendingCoffeeChatUseCase.finallyApprove(any()) }

            patchRequest(baseUrl, arrayOf(1L)) {
                accessToken(mentor)
                bodyContent(request)
            }.andExpect {
                status { isNoContent() }
            }.andDo {
                makeSuccessDocsWithAccessToken("CoffeeChatApi/LifeCycle/PendingCoffeeChat/Approve/Success") {
                    pathParameters(*pathParameters)
                    requestFields(*requestFields)
                }
            }
        }
    }
}
