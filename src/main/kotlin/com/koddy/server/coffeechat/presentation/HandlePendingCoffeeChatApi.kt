package com.koddy.server.coffeechat.presentation

import com.koddy.server.auth.domain.model.Authenticated
import com.koddy.server.coffeechat.application.usecase.HandlePendingCoffeeChatUseCase
import com.koddy.server.coffeechat.presentation.request.FinallyApprovePendingCoffeeChatRequest
import com.koddy.server.coffeechat.presentation.request.FinallyCancelPendingCoffeeChatRequest
import com.koddy.server.global.annotation.Auth
import com.koddy.server.global.aop.AccessControl
import com.koddy.server.member.domain.model.Role
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "4-4-3. Pending 상태인 커피챗에 대한 멘토의 최종 결정")
@RestController
@RequestMapping("/api/coffeechats/pending")
class HandlePendingCoffeeChatApi(
    private val handlePendingCoffeeChatUseCase: HandlePendingCoffeeChatUseCase,
) {
    @Operation(summary = "Pending 상태 커피챗 최종 취소 Endpoint")
    @PatchMapping("/cancel/{coffeeChatId}")
    @AccessControl(role = Role.MENTOR)
    fun finallyCancel(
        @Auth authenticated: Authenticated,
        @PathVariable coffeeChatId: Long,
        @RequestBody @Valid request: FinallyCancelPendingCoffeeChatRequest,
    ): ResponseEntity<Void> {
        handlePendingCoffeeChatUseCase.finallyCancel(
            request.toCommand(
                mentorId = authenticated.id,
                coffeeChatId = coffeeChatId,
            ),
        )
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "Pending 상태 커피챗 최종 수락 Endpoint")
    @PatchMapping("/approve/{coffeeChatId}")
    @AccessControl(role = Role.MENTOR)
    fun finallyApprove(
        @Auth authenticated: Authenticated,
        @PathVariable coffeeChatId: Long,
        @RequestBody @Valid request: FinallyApprovePendingCoffeeChatRequest,
    ): ResponseEntity<Void> {
        handlePendingCoffeeChatUseCase.finallyApprove(
            request.toCommand(
                mentorId = authenticated.id,
                coffeeChatId = coffeeChatId,
            ),
        )
        return ResponseEntity.noContent().build()
    }
}
