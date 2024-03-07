package com.koddy.server.coffeechat.presentation

import com.koddy.server.auth.domain.model.Authenticated
import com.koddy.server.coffeechat.application.usecase.GetCoffeeChatScheduleUseCase
import com.koddy.server.coffeechat.application.usecase.query.response.CoffeeChatEachCategoryCounts
import com.koddy.server.coffeechat.presentation.request.GetCoffeeChatScheduleRequest
import com.koddy.server.global.annotation.Auth
import com.koddy.server.global.query.SliceResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "4-5. 내 일정 관련 조회 API")
@RestController
@RequestMapping("/api/coffeechats")
class CoffeeChatScheduleQueryApi(
    private val getCoffeeChatScheduleUseCase: GetCoffeeChatScheduleUseCase,
) {
    @Operation(summary = "내 일정 상태별 커피챗 개수 조회")
    @GetMapping("/me/category-counts")
    fun getEachCategoryCounts(
        @Auth authenticated: Authenticated,
    ): ResponseEntity<CoffeeChatEachCategoryCounts> {
        val result: CoffeeChatEachCategoryCounts = getCoffeeChatScheduleUseCase.getEachCategoryCounts(authenticated)
        return ResponseEntity.ok(result)
    }

    @Operation(summary = "내 일정 상태별 커피챗 정보 조회")
    @GetMapping("/me/schedules")
    fun getSchedules(
        @Auth authenticated: Authenticated,
        @ModelAttribute @Valid request: GetCoffeeChatScheduleRequest,
    ): ResponseEntity<SliceResponse<*>> {
        val result: SliceResponse<*> = when (authenticated.isMentor) {
            true -> getCoffeeChatScheduleUseCase.getMentorSchedules(request.toMentorQuery(authenticated.id))
            false -> getCoffeeChatScheduleUseCase.getMenteeSchedules(request.toMenteeQuery(authenticated.id))
        }
        return ResponseEntity.ok(result)
    }
}
