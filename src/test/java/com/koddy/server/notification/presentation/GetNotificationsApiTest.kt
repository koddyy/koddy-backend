package com.koddy.server.notification.presentation

import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus
import com.koddy.server.common.ApiDocsTestKt
import com.koddy.server.common.docs.BOOLEAN
import com.koddy.server.common.docs.DocumentField
import com.koddy.server.common.docs.NUMBER
import com.koddy.server.common.docs.STRING
import com.koddy.server.common.toLocalDate
import com.koddy.server.common.toLocalDateTime
import com.koddy.server.global.query.SliceResponse
import com.koddy.server.notification.application.usecase.GetNotificationsUseCase
import com.koddy.server.notification.application.usecase.query.response.NotificationSummary
import com.koddy.server.notification.domain.model.NotificationType
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest

@WebMvcTest(GetNotificationsApi::class)
@DisplayName("Notification -> GetNotificationsApi 테스트")
internal class GetNotificationsApiTest : ApiDocsTestKt() {
    @MockkBean
    private lateinit var getNotificationsUseCase: GetNotificationsUseCase

    @Nested
    @DisplayName("알림 조회 API [GET /api/notifications/me]")
    internal inner class GetNotifications {
        private val baseUrl = "/api/notifications/me"

        private val queryParameters: Array<DocumentField> = arrayOf(
            "page" type NUMBER means "페이지" constraint "1부터 시작",
        )
        private val responseFields: Array<DocumentField> = arrayOf(
            "result[].id" type NUMBER means "알림 ID(PK)",
            "result[].isRead" type BOOLEAN means "알림 읽음 여부",
            "result[].type" type STRING means "알림 타입",
            "result[].createdAt" type STRING means "알림 생성 시간",
            "result[].member.id" type NUMBER means "사용자 ID(PK)",
            "result[].member.name" type STRING means "사용자 이름",
            "result[].member.profileImageUrl" type STRING means "사용자 프로필 이미지 URL" constraint "Nullable",
            "result[].coffeeChat.id" type NUMBER means "커피챗 ID(PK)",
            "result[].coffeeChat.statusSnapshot" type STRING means "알림 시점 커피챗 상태 스냅샷",
            "result[].coffeeChat.cancelReason" type STRING means "커피챗 취소 사유" constraint "Nullable",
            "result[].coffeeChat.rejectReason" type STRING means "커피챗 거절 사유" constraint "Nullable",
            "result[].coffeeChat.reservedDay" type STRING means "커피챗 예약 날짜" constraint "Nullable",
            "hasNext" type BOOLEAN means "다음 스크롤 존재 여부",
        )

        @Test
        fun `알림을 조회한다`() {
            val response = SliceResponse(
                listOf(
                    NotificationSummary(
                        id = 1L,
                        isRead = false,
                        type = NotificationType.MENTOR_RECEIVE_MENTEE_APPLY_FROM_MENTEE_FLOW.name,
                        createdAt = "2024/02/25-18:58:32".toLocalDateTime(),
                        member = NotificationSummary.NotifyMember(
                            id = 1L,
                            name = "이름",
                            profileImageUrl = "프로필 이미지 URL",
                        ),
                        coffeeChat = NotificationSummary.NotifyCoffeeChat(
                            id = 1L,
                            statusSnapshot = CoffeeChatStatus.MENTEE_APPLY.name,
                            cancelReason = "취소 사유..",
                            rejectReason = "거절 사유..",
                            reservedDay = "2024/03/01".toLocalDate(),
                        ),
                    ),
                ),
                false,
            )
            every { getNotificationsUseCase.invoke(any()) } returns response

            getRequest(baseUrl) {
                accessToken(common)
                param("page", "1")
            }.andExpect {
                status { isOk() }
                content { success(response) }
            }.andDo {
                makeSuccessDocsWithAccessToken("NotificationApi/GetMyNotifications") {
                    queryParameters(*queryParameters)
                    responseFields(*responseFields)
                }
            }
        }
    }
}
