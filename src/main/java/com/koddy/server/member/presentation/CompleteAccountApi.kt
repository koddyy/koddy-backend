package com.koddy.server.member.presentation

import com.koddy.server.auth.domain.model.Authenticated
import com.koddy.server.global.annotation.Auth
import com.koddy.server.global.aop.AccessControl
import com.koddy.server.member.application.usecase.CompleteProfileUseCase
import com.koddy.server.member.domain.model.Role
import com.koddy.server.member.presentation.dto.request.CompleteMenteeProfileRequest
import com.koddy.server.member.presentation.dto.request.CompleteMentorProfileRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "2-2. 사용자 프로필 완성 API")
@RestController
@RequestMapping("/api")
class CompleteAccountApi(
    private val completeProfileUseCase: CompleteProfileUseCase,
) {
    @Operation(summary = "멘토 프로필 완성 Endpoint")
    @PatchMapping("/mentors/me/complete")
    @AccessControl(role = Role.MENTOR)
    fun completeMentor(
        @Auth authenticated: Authenticated,
        @RequestBody @Valid request: CompleteMentorProfileRequest,
    ): ResponseEntity<Unit> {
        completeProfileUseCase.completeMentor(request.toCommand(authenticated.id))
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "멘티 프로필 완성 Endpoint")
    @PatchMapping("/mentees/me/complete")
    @AccessControl(role = Role.MENTEE)
    fun completeMentee(
        @Auth authenticated: Authenticated,
        @RequestBody @Valid request: CompleteMenteeProfileRequest,
    ): ResponseEntity<Unit> {
        completeProfileUseCase.completeMentee(request.toCommand(authenticated.id))
        return ResponseEntity.noContent().build()
    }
}
