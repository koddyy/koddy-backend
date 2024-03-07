package com.koddy.server.coffeechat.presentation

import com.koddy.server.auth.domain.model.Authenticated
import com.koddy.server.coffeechat.application.usecase.CreateCoffeeChatUseCase
import com.koddy.server.coffeechat.presentation.request.CreateCoffeeChatByApplyRequest
import com.koddy.server.coffeechat.presentation.request.CreateCoffeeChatBySuggestRequest
import com.koddy.server.global.ResponseWrapper
import com.koddy.server.global.annotation.Auth
import com.koddy.server.global.aop.AccessControl
import com.koddy.server.member.domain.model.Role
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "4-2. 커피챗 신청/제안 API")
@RestController
@RequestMapping("/api/coffeechats")
class CreateCoffeeChatApi(
    private val createCoffeeChatUseCase: CreateCoffeeChatUseCase,
) {
    @Operation(summary = "멘티 -> 멘토 커피챗 신청 Endpoint")
    @PostMapping("/apply")
    @AccessControl(role = Role.MENTEE)
    fun createByApply(
        @Auth authenticated: Authenticated,
        @RequestBody @Valid request: CreateCoffeeChatByApplyRequest,
    ): ResponseEntity<ResponseWrapper<Long>> {
        val coffeeChatId: Long = createCoffeeChatUseCase.createByApply(request.toCommand(authenticated.id))
        return ResponseEntity.ok(ResponseWrapper(coffeeChatId))
    }

    @Operation(summary = "멘토 -> 멘티 커피챗 제안 Endpoint")
    @PostMapping("/suggest")
    @AccessControl(role = Role.MENTOR)
    fun createBySuggest(
        @Auth authenticated: Authenticated,
        @RequestBody @Valid request: CreateCoffeeChatBySuggestRequest,
    ): ResponseEntity<ResponseWrapper<Long>> {
        val coffeeChatId: Long = createCoffeeChatUseCase.createBySuggest(request.toCommand(authenticated.id))
        return ResponseEntity.ok(ResponseWrapper(coffeeChatId))
    }
}
