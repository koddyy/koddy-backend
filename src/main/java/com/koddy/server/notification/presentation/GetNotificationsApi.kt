package com.koddy.server.notification.presentation

import com.koddy.server.auth.domain.model.Authenticated
import com.koddy.server.global.annotation.Auth
import com.koddy.server.global.query.SliceResponse
import com.koddy.server.notification.application.usecase.GetNotificationsUseCase
import com.koddy.server.notification.application.usecase.query.GetNotifications
import com.koddy.server.notification.application.usecase.query.response.NotificationSummary
import com.koddy.server.notification.presentation.request.GetNotificationsRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "5-2. 알림 조회 API")
@RestController
@RequestMapping("/api/notifications/me")
class GetNotificationsApi(
    private val getNotificationsUseCase: GetNotificationsUseCase,
) {
    @Operation(summary = "알림 조회 Endpoint")
    @GetMapping
    fun getNotifications(
        @Auth authenticated: Authenticated,
        @ModelAttribute @Valid request: GetNotificationsRequest,
    ): ResponseEntity<SliceResponse<List<NotificationSummary>>> {
        val result: SliceResponse<List<NotificationSummary>> = getNotificationsUseCase.invoke(
            GetNotifications(
                authenticated = authenticated,
                page = request.page,
            ),
        )
        return ResponseEntity.ok(result)
    }
}
