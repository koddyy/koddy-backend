package com.koddy.server.notification.presentation

import com.koddy.server.auth.domain.model.Authenticated
import com.koddy.server.global.annotation.Auth
import com.koddy.server.notification.application.usecase.ReadNotificationUseCase
import com.koddy.server.notification.application.usecase.command.ReadSingleNotificationCommand
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "5-1. 알림 처리 API")
@RestController
@RequestMapping("/api/notifications")
class ReadNotificationApi(
    private val readNotificationUseCase: ReadNotificationUseCase,
) {
    @Operation(summary = "알림 단건 읽음 처리 Endpoint")
    @PatchMapping("/{notificationId}/read")
    fun readSingle(
        @Auth authenticated: Authenticated,
        @PathVariable notificationId: Long,
    ): ResponseEntity<Void> {
        readNotificationUseCase.readSingle(
            ReadSingleNotificationCommand(
                memberId = authenticated.id,
                notificationId = notificationId,
            ),
        )
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "알림 전체 읽음 처리 Endpoint")
    @PatchMapping("/me/read-all")
    fun readAll(
        @Auth authenticated: Authenticated,
    ): ResponseEntity<Void> {
        readNotificationUseCase.readAll(memberId = authenticated.id)
        return ResponseEntity.noContent().build()
    }
}
