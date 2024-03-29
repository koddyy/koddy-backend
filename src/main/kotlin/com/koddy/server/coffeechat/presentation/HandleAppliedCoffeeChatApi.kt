package com.koddy.server.coffeechat.presentation

import com.koddy.server.auth.domain.model.Authenticated
import com.koddy.server.coffeechat.application.usecase.HandleAppliedCoffeeChatUseCase
import com.koddy.server.coffeechat.presentation.request.ApproveAppliedCoffeeChatRequest
import com.koddy.server.coffeechat.presentation.request.RejectAppliedCoffeeChatRequest
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

@Tag(name = "4-4-1. 멘티가 신청한 커피챗 처리 API [멘토 전용]")
@RestController
@RequestMapping("/api/coffeechats/applied")
class HandleAppliedCoffeeChatApi(
    private val handleAppliedCoffeeChatUseCase: HandleAppliedCoffeeChatUseCase,
) {
    @Operation(summary = "멘티가 신청한 커피챗 거절 Endpoint")
    @PatchMapping("/reject/{coffeeChatId}")
    @AccessControl(role = Role.MENTOR)
    fun reject(
        @Auth authenticated: Authenticated,
        @PathVariable coffeeChatId: Long,
        @RequestBody @Valid request: RejectAppliedCoffeeChatRequest,
    ): ResponseEntity<Void> {
        handleAppliedCoffeeChatUseCase.reject(
            request.toCommand(
                mentorId = authenticated.id,
                coffeeChatId = coffeeChatId,
            ),
        )
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "멘티가 신청한 커피챗 수락 Endpoint")
    @PatchMapping("/approve/{coffeeChatId}")
    @AccessControl(role = Role.MENTOR)
    fun approve(
        @Auth authenticated: Authenticated,
        @PathVariable coffeeChatId: Long,
        @RequestBody @Valid request: ApproveAppliedCoffeeChatRequest,
    ): ResponseEntity<Void> {
        handleAppliedCoffeeChatUseCase.approve(
            request.toCommand(
                mentorId = authenticated.id,
                coffeeChatId = coffeeChatId,
            ),
        )
        return ResponseEntity.noContent().build()
    }
}
