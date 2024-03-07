package com.koddy.server.coffeechat.presentation

import com.koddy.server.auth.domain.model.Authenticated
import com.koddy.server.coffeechat.application.usecase.CancelCoffeeChatUseCase
import com.koddy.server.coffeechat.presentation.request.CancelCoffeeChatRequest
import com.koddy.server.global.annotation.Auth
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "4-3. 신청/제안한 커피챗 취소 API")
@RestController
@RequestMapping("/api/coffeechats/cancel/{coffeeChatId}")
class CancelCoffeeChatApi(
    private val cancelCoffeeChatUseCase: CancelCoffeeChatUseCase,
) {
    @Operation(summary = "신청/제안한 커피챗 취소 Endpoint")
    @PatchMapping
    fun cancel(
        @Auth authenticated: Authenticated,
        @PathVariable coffeeChatId: Long,
        @RequestBody @Valid request: CancelCoffeeChatRequest,
    ): ResponseEntity<Void> {
        cancelCoffeeChatUseCase.invoke(
            request.toCommand(
                authenticated = authenticated,
                coffeeChatId = coffeeChatId,
            ),
        )
        return ResponseEntity.noContent().build()
    }
}
