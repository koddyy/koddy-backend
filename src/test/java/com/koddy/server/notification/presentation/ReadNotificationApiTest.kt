package com.koddy.server.notification.presentation

import com.koddy.server.common.ApiDocsTestKt
import com.koddy.server.common.docs.NUMBER
import com.koddy.server.notification.application.usecase.ReadNotificationUseCase
import com.ninjasquad.springmockk.MockkBean
import io.mockk.justRun
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest

@WebMvcTest(ReadNotificationApi::class)
@DisplayName("Notification -> ReadNotificationApi 테스트")
internal class ReadNotificationApiTest : ApiDocsTestKt() {
    @MockkBean
    private lateinit var readNotificationUseCase: ReadNotificationUseCase

    @Nested
    @DisplayName("알림 단건 읽음 처리 API [PATCH /api/notifications/{notificationId}/read]")
    internal inner class ReadSingle {
        private val baseUrl = "/api/notifications/{notificationId}/read"

        @Test
        fun `알림 단건 읽음 처리를 진행한다`() {
            justRun { readNotificationUseCase.readSingle(any()) }

            patchRequest(baseUrl, arrayOf(1L)) {
                accessToken(common)
            }.andExpect {
                status { isNoContent() }
            }.andDo {
                makeSuccessDocsWithAccessToken("NotificationApi/ReadProcessing/Single") {
                    pathParameters(
                        "notificationId" type NUMBER means "알림 ID(PK)",
                    )
                }
            }
        }
    }

    @Nested
    @DisplayName("알림 전체 읽음 처리 API [PATCH /api/notifications/me/read-all]")
    internal inner class ReadAll {
        private val baseUrl = "/api/notifications/me/read-all"

        @Test
        fun `알림 전체 읽음 처리를 진행한다`() {
            justRun { readNotificationUseCase.readAll(any()) }

            patchRequest(baseUrl) {
                accessToken(common)
            }.andExpect {
                status { isNoContent() }
            }.andDo {
                makeSuccessDocsWithAccessToken("NotificationApi/ReadProcessing/All") {}
            }
        }
    }
}
