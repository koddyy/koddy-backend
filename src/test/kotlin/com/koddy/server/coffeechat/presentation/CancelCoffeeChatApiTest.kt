package com.koddy.server.coffeechat.presentation

import com.koddy.server.coffeechat.application.usecase.CancelCoffeeChatUseCase
import com.koddy.server.coffeechat.exception.CoffeeChatException
import com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode
import com.koddy.server.coffeechat.presentation.request.CancelCoffeeChatRequest
import com.koddy.server.common.ApiDocsTestKt
import com.koddy.server.common.docs.DocumentField
import com.koddy.server.common.docs.NUMBER
import com.koddy.server.common.docs.STRING
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.justRun
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest

@WebMvcTest(CancelCoffeeChatApi::class)
@DisplayName("CoffeeChat -> CancelCoffeeChatApi 테스트")
internal class CancelCoffeeChatApiTest : ApiDocsTestKt() {
    @MockkBean
    private lateinit var cancelCoffeeChatUseCase: CancelCoffeeChatUseCase

    @Nested
    @DisplayName("신청/제안한 커피챗 취소 API [PATCH /api/coffeechats/cancel/{coffeeChatId}]")
    internal inner class Cancel {
        private val baseUrl = "/api/coffeechats/cancel/{coffeeChatId}"
        private val request = CancelCoffeeChatRequest(cancelReason = "취소..")

        private val pathParameters: Array<DocumentField> = arrayOf(
            "coffeeChatId" type NUMBER means "커피챗 ID(PK)",
        )
        private val requestFields: Array<DocumentField> = arrayOf(
            "cancelReason" type STRING means "취소 사유",
        )

        @Test
        fun `해당 커피챗이 취소될 수 없는 상태면 취소가 불가능하다`() {
            val exceptionCode = CoffeeChatExceptionCode.CANNOT_CANCEL_STATUS
            every { cancelCoffeeChatUseCase.invoke(any()) } throws CoffeeChatException(exceptionCode)

            patchRequest(baseUrl, arrayOf(1L)) {
                accessToken(common)
                bodyContent(request)
            }.andExpect {
                status { isConflict() }
                content { exception(exceptionCode) }
            }.andDo {
                makeFailureDocsWithAccessToken("CoffeeChatApi/LifeCycle/Cancel/Failure") {
                    pathParameters(*pathParameters)
                    requestFields(*requestFields)
                }
            }
        }

        @Test
        fun `커피챗을 취소한다`() {
            justRun { cancelCoffeeChatUseCase.invoke(any()) }

            patchRequest(baseUrl, arrayOf(1L)) {
                accessToken(common)
                bodyContent(request)
            }.andExpect {
                status { isNoContent() }
            }.andDo {
                makeSuccessDocsWithAccessToken("CoffeeChatApi/LifeCycle/Cancel/Success") {
                    pathParameters(*pathParameters)
                    requestFields(*requestFields)
                }
            }
        }
    }
}
