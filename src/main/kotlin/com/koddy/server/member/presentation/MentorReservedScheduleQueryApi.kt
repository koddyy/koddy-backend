package com.koddy.server.member.presentation

import com.koddy.server.auth.domain.model.Authenticated
import com.koddy.server.global.annotation.Auth
import com.koddy.server.member.application.usecase.GetMentorReservedScheduleUseCase
import com.koddy.server.member.application.usecase.query.response.MentorReservedSchedule
import com.koddy.server.member.presentation.request.GetMentorReservedScheduleRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "2-6. 멘토 스케줄 조회 API")
@RestController
@RequestMapping("/api/mentors/{mentorId}/reserved-schedule")
class MentorReservedScheduleQueryApi(
    private val getMentorReservedScheduleUseCase: GetMentorReservedScheduleUseCase,
) {
    @Operation(summary = "특정 Year-Month에 대해서 멘토의 예약된 스케줄 조회 Endpoint")
    @GetMapping
    fun getMentorReservedSchedule(
        @Auth authenticated: Authenticated,
        @PathVariable mentorId: Long,
        @ModelAttribute @Valid request: GetMentorReservedScheduleRequest,
    ): ResponseEntity<MentorReservedSchedule> {
        val result: MentorReservedSchedule = getMentorReservedScheduleUseCase.invoke(request.toQuery(mentorId))
        return ResponseEntity.ok(result)
    }
}
