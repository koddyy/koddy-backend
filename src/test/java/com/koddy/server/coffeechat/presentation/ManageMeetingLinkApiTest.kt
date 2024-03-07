package com.koddy.server.coffeechat.presentation

import com.koddy.server.auth.exception.AuthExceptionCode
import com.koddy.server.coffeechat.application.usecase.ManageMeetingLinkUseCase
import com.koddy.server.coffeechat.infrastructure.link.zoom.spec.ZoomMeetingLinkResponse
import com.koddy.server.coffeechat.presentation.request.CreateMeetingLinkRequest
import com.koddy.server.coffeechat.presentation.response.CreateMeetingLinkResponse
import com.koddy.server.common.ApiDocsTestKt
import com.koddy.server.common.docs.DocumentField
import com.koddy.server.common.docs.NUMBER
import com.koddy.server.common.docs.STRING
import com.koddy.server.common.toLocalDateTime
import com.koddy.server.common.utils.OAuthDummy.AUTHORIZATION_CODE
import com.koddy.server.common.utils.OAuthDummy.REDIRECT_URI
import com.koddy.server.common.utils.OAuthDummy.STATE
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.justRun
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest

@WebMvcTest(ManageMeetingLinkApi::class)
@DisplayName("CoffeeChat -> ManageMeetingLinkApi 테스트")
internal class ManageMeetingLinkApiTest : ApiDocsTestKt() {
    @MockkBean
    private lateinit var manageMeetingLinkUseCase: ManageMeetingLinkUseCase

    @Nested
    @DisplayName("커피챗 링크 생성 API [POST /api/oauth/{provider}/meetings]")
    internal inner class CreateMeetingLink {
        private val baseUrl = "/api/oauth/{provider}/meetings"
        private val request = CreateMeetingLinkRequest(
            authorizationCode = AUTHORIZATION_CODE,
            redirectUri = REDIRECT_URI,
            state = STATE,
            topic = "xxxyyy와 멘토링 시간",
            start = "2024/3/1-20:00".toLocalDateTime().toString(),
            end = "2024/3/1-20:30".toLocalDateTime().toString(),
        )

        private val pathParameters: Array<DocumentField> = arrayOf(
            "provider" type STRING means "OAuth & Meeting Link Provider" constraint "zoom",
        )
        private val requestFields: Array<DocumentField> = arrayOf(
            "authorizationCode" type STRING means "Authorization Code" constraint "QueryParam -> code",
            "redirectUri" type STRING means "Redirect Uri" constraint "Authorization Code 요청 URI와 동일 값",
            "state" type STRING means "State 값" constraint "QueryParam -> state",
            "topic" type STRING means "회의 제목",
            "start" type STRING means "회의 시작 시간" constraint "[KST] yyyy-MM-ddTHH:mm:ss $enter -> 시간 = 00:00:00 ~ 23:59:59",
            "end" type STRING means "회의 종료 시간" constraint "[KST] yyyy-MM-ddTHH:mm:ss $enter -> 시간 = 00:00:00 ~ 23:59:59",
        )
        private val responseFields: Array<DocumentField> = arrayOf(
            "id" type STRING means "미팅 ID",
            "hostEmail" type STRING means "호스트 이메일",
            "topic" type STRING means "회의 제목",
            "joinUrl" type STRING means "회의 참여 URL",
            "duration" type NUMBER means "회의 진행 시간 (Minute 기준)",
        )

        @Test
        fun `멘토가 아니면 권한이 없다`() {
            val exceptionCode = AuthExceptionCode.INVALID_PERMISSION

            postRequest(baseUrl, arrayOf("zoom")) {
                accessToken(mentee)
                bodyContent(request)
            }.andExpect {
                status { isForbidden() }
                content { exception(exceptionCode) }
            }.andDo {
                makeFailureDocsWithAccessToken("CoffeeChatApi/MeetingLink/Create/Failure") {
                    pathParameters(*pathParameters)
                    requestFields(*requestFields)
                }
            }
        }

        @Test
        fun `커피챗 링크를 생성한다`() {
            val response = ZoomMeetingLinkResponse(
                id = "88141392261",
                hostEmail = "sjiwon4491@gmail.com",
                topic = "xxxyyy와 멘토링 시간",
                joinUrl = "https://us05web.zoom.us/j/88141392261?pwd=...",
                duration = 60,
            )
            every { manageMeetingLinkUseCase.create(any()) } returns response

            postRequest(baseUrl, arrayOf("zoom")) {
                accessToken(mentor)
                bodyContent(request)
            }.andExpect {
                status { isOk() }
                content {
                    success(
                        CreateMeetingLinkResponse(
                            id = response.id,
                            hostEmail = response.hostEmail,
                            topic = response.topic,
                            joinUrl = response.joinUrl,
                            duration = response.duration,
                        ),
                    )
                }
            }.andDo {
                makeSuccessDocsWithAccessToken("CoffeeChatApi/MeetingLink/Create/Success") {
                    pathParameters(*pathParameters)
                    requestFields(*requestFields)
                    responseFields(*responseFields)
                }
            }
        }
    }

    @Nested
    @DisplayName("커피챗 링크 삭제 API [DELETE /api/oauth/{provider}/meetings/{meetingId}]")
    internal inner class DeleteMeetingLink {
        private val baseUrl = "/api/oauth/{provider}/meetings/{meetingId}"

        private val pathParameters: Array<DocumentField> = arrayOf(
            "provider" type STRING means "OAuth & Meeting Link Provider" constraint "zoom",
            "meetingId" type STRING means "미팅 ID" constraint "생성 시 제공한 미팅 ID 그대로 요청 + 10분 동안 유효",
        )

        @Test
        fun `멘토가 아니면 권한이 없다`() {
            val exceptionCode = AuthExceptionCode.INVALID_PERMISSION

            deleteRequest(baseUrl, arrayOf("zoom", "88141392261")) {
                accessToken(mentee)
            }.andExpect {
                status { isForbidden() }
                content { exception(exceptionCode) }
            }.andDo {
                makeFailureDocsWithAccessToken("CoffeeChatApi/MeetingLink/Delete/Failure") {
                    pathParameters(*pathParameters)
                }
            }
        }

        @Test
        fun `생성한 커피챗 링크를 삭제한다`() {
            justRun { manageMeetingLinkUseCase.delete(any()) }

            deleteRequest(baseUrl, arrayOf("zoom", "88141392261")) {
                accessToken(mentor)
            }.andExpect {
                status { isNoContent() }
            }.andDo {
                makeSuccessDocsWithAccessToken("CoffeeChatApi/MeetingLink/Delete/Success") {
                    pathParameters(*pathParameters)
                }
            }
        }
    }
}
