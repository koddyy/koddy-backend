package com.koddy.server.acceptance.notification

import com.koddy.server.acceptance.RequestHelper
import com.koddy.server.notification.application.usecase.query.response.NotificationSummary
import com.koddy.server.notification.domain.model.NotificationType
import io.restassured.response.ValidatableResponse

object NotificationAcceptanceStep {
    fun 알림을_조회한다(
        page: Int,
        accessToken: String,
    ): ValidatableResponse =
        RequestHelper.getRequestWithAccessToken(
            uri = "/api/notifications/me?page=$page",
            accessToken = accessToken,
        )

    fun 특정_타입의_알림_ID를_조회한다(
        type: NotificationType,
        page: Int,
        accessToken: String,
    ): Long =
        알림을_조회한다(page, accessToken)
            .extract()
            .jsonPath()
            .getList("result", NotificationSummary::class.java)
            .first { it.type == type.name }
            .id

    fun 단건_알림을_읽음_처리한다(
        notificationId: Long,
        accessToken: String,
    ): ValidatableResponse =
        RequestHelper.patchRequestWithAccessToken(
            uri = "/api/notifications/$notificationId/read",
            accessToken = accessToken,
        )

    fun 전체_알림을_읽음_처리한다(accessToken: String): ValidatableResponse =
        RequestHelper.patchRequestWithAccessToken(
            uri = "/api/notifications/me/read-all",
            accessToken = accessToken,
        )
}
