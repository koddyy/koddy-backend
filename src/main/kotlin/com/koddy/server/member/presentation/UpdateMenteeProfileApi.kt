package com.koddy.server.member.presentation

import com.koddy.server.auth.domain.model.Authenticated
import com.koddy.server.global.annotation.Auth
import com.koddy.server.global.aop.AccessControl
import com.koddy.server.member.application.usecase.UpdateMenteeProfileUseCase
import com.koddy.server.member.domain.model.Role
import com.koddy.server.member.presentation.request.UpdateMenteeBasicInfoRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "2-4. 멘티 정보 수정 API")
@RestController
@RequestMapping("/api/mentees/me")
class UpdateMenteeProfileApi(
    private val updateMenteeProfileUseCase: UpdateMenteeProfileUseCase,
) {
    @Operation(summary = "멘티 기본정보 수정 Endpoint")
    @PatchMapping("/basic-info")
    @AccessControl(role = Role.MENTEE)
    fun updateBasicInfo(
        @Auth authenticated: Authenticated,
        @RequestBody @Valid request: UpdateMenteeBasicInfoRequest,
    ): ResponseEntity<Unit> {
        updateMenteeProfileUseCase.updateBasicInfo(request.toCommand(authenticated.id))
        return ResponseEntity.noContent().build()
    }
}
