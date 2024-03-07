package com.koddy.server.coffeechat.presentation

import com.koddy.server.auth.exception.AuthExceptionCode
import com.koddy.server.coffeechat.application.usecase.HandleAppliedCoffeeChatUseCase
import com.koddy.server.coffeechat.domain.model.Strategy
import com.koddy.server.coffeechat.presentation.request.ApproveAppliedCoffeeChatRequest
import com.koddy.server.coffeechat.presentation.request.RejectAppliedCoffeeChatRequest
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

@WebMvcTest(HandleAppliedCoffeeChatApi::class)
@DisplayName("CoffeeChat -> HandleAppliedCoffeeChatApi 테스트")
internal class HandleAppliedCoffeeChatApiTest : ApiDocsTestKt() {
    @MockkBean
    private lateinit var handleAppliedCoffeeChatUseCase: HandleAppliedCoffeeChatUseCase

    @Nested
    @DisplayName("멘티가 신청한 커피챗 거절 API [PATCH /api/coffeechats/applied/reject/{coffeeChatId}]")
    internal inner class Reject {
        private val baseUrl = "/api/coffeechats/applied/reject/{coffeeChatId}"
        private val request = RejectAppliedCoffeeChatRequest(rejectReason = "거절..")

        private val pathParameters: Array<DocumentField> = arrayOf(
            "coffeeChatId" type NUMBER means "커피챗 ID(PK)",
        )
        private val requestFields: Array<DocumentField> = arrayOf(
            "rejectReason" type STRING means "거절 사유",
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
                makeFailureDocsWithAccessToken("CoffeeChatApi/LifeCycle/AppliedByMentee/Reject/Failure") {
                    pathParameters(*pathParameters)
                    requestFields(*requestFields)
                }
            }
        }

        @Test
        fun `멘토는 멘티가 신청한 커피챗을 거절한다`() {
            justRun { handleAppliedCoffeeChatUseCase.reject(any()) }

            patchRequest(baseUrl, arrayOf(1L)) {
                accessToken(mentor)
                bodyContent(request)
            }.andExpect {
                status { isNoContent() }
            }.andDo {
                makeSuccessDocsWithAccessToken("CoffeeChatApi/LifeCycle/AppliedByMentee/Reject/Success") {
                    pathParameters(*pathParameters)
                    requestFields(*requestFields)
                }
            }
        }
    }

    @Nested
    @DisplayName("멘티가 신청한 커피챗 수락 API [PATCH /api/coffeechats/applied/approve/{coffeeChatId}]")
    internal inner class Approve {
        private val baseUrl = "/api/coffeechats/applied/approve/{coffeeChatId}"
        private val request = ApproveAppliedCoffeeChatRequest(
            question = "궁금한 점..",
            chatType = Strategy.Type.KAKAO_ID.value,
            chatValue = "sjiwon",
        )

        private val pathParameters: Array<DocumentField> = arrayOf(
            "coffeeChatId" type NUMBER means "커피챗 ID(PK)",
        )
        private val requestFields: Array<DocumentField> = arrayOf(
            "question" type STRING means "멘티에게 궁금한 점",
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
                makeFailureDocsWithAccessToken("CoffeeChatApi/LifeCycle/AppliedByMentee/Approve/Failure") {
                    pathParameters(*pathParameters)
                    requestFields(*requestFields)
                }
            }
        }

        @Test
        fun `멘토는 멘티가 신청한 커피챗을 수락한다`() {
            justRun { handleAppliedCoffeeChatUseCase.approve(any()) }

            patchRequest(baseUrl, arrayOf(1L)) {
                accessToken(mentor)
                bodyContent(request)
            }.andExpect {
                status { isNoContent() }
            }.andDo {
                makeSuccessDocsWithAccessToken("CoffeeChatApi/LifeCycle/AppliedByMentee/Approve/Success") {
                    pathParameters(*pathParameters)
                    requestFields(*requestFields)
                }
            }
        }
    }
}
