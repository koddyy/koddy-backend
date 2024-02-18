package com.koddy.server.member.presentation

import com.koddy.server.auth.domain.model.Authenticated
import com.koddy.server.global.annotation.Auth
import com.koddy.server.global.aop.AccessControl
import com.koddy.server.member.application.usecase.UpdateMentorInfoUseCase
import com.koddy.server.member.domain.model.Role
import com.koddy.server.member.presentation.request.UpdateMentorBasicInfoRequest
import com.koddy.server.member.presentation.request.UpdateMentorScheduleRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "2-3. 멘토 정보 수정 API")
@RestController
@RequestMapping("/api/mentors/me")
class UpdateMentorInfoApi(
    private val updateMentorInfoUseCase: UpdateMentorInfoUseCase,
) {
    @Operation(summary = "멘토 기본정보 수정 Endpoint")
    @PatchMapping("/basic-info")
    @AccessControl(role = Role.MENTOR)
    fun updateBasicInfo(
        @Auth authenticated: Authenticated,
        @RequestBody @Valid request: UpdateMentorBasicInfoRequest,
    ): ResponseEntity<Unit> {
        updateMentorInfoUseCase.updateBasicInfo(request.toCommand(authenticated.id))
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "멘토 스케줄 수정 Endpoint")
    @PatchMapping("/schedules")
    @AccessControl(role = Role.MENTOR)
    fun updateSchedule(
        @Auth authenticated: Authenticated,
        @RequestBody @Valid request: UpdateMentorScheduleRequest,
    ): ResponseEntity<Unit> {
        updateMentorInfoUseCase.updateSchedule(request.toCommand(authenticated.id))
        return ResponseEntity.noContent().build()
    }
}
