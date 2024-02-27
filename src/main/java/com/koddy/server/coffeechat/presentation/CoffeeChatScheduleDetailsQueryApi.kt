package com.koddy.server.coffeechat.presentation

import com.koddy.server.auth.domain.model.Authenticated
import com.koddy.server.coffeechat.application.usecase.GetCoffeeChatScheduleDetailsUseCase
import com.koddy.server.coffeechat.application.usecase.query.GetCoffeeChatScheduleDetails
import com.koddy.server.coffeechat.domain.model.response.CoffeeChatScheduleDetails
import com.koddy.server.global.annotation.Auth
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "4-6. 내 일정 커피챗 상세 조회 API")
@RestController
@RequestMapping("/api/coffeechats/{coffeeChatId}")
class CoffeeChatScheduleDetailsQueryApi(
    private val getCoffeeChatScheduleDetailsUseCase: GetCoffeeChatScheduleDetailsUseCase,
) {
    @Operation(summary = "내 일정 커피챗 상세 조회 Endpoint")
    @GetMapping
    fun getCoffeeChatScheduleDetails(
        @Auth authenticated: Authenticated,
        @PathVariable coffeeChatId: Long,
    ): ResponseEntity<CoffeeChatScheduleDetails> {
        val result: CoffeeChatScheduleDetails = getCoffeeChatScheduleDetailsUseCase.invoke(
            GetCoffeeChatScheduleDetails(
                authenticated,
                coffeeChatId,
            ),
        )
        return ResponseEntity.ok(result)
    }
}
