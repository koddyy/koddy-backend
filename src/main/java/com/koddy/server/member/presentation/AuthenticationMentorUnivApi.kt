package com.koddy.server.member.presentation

import com.koddy.server.auth.domain.model.Authenticated
import com.koddy.server.global.annotation.Auth
import com.koddy.server.global.aop.AccessControl
import com.koddy.server.global.aop.DailyMailAuthLimit
import com.koddy.server.global.utils.UniversityInfo
import com.koddy.server.member.application.usecase.AuthenticationMentorUnivUseCase
import com.koddy.server.member.domain.model.Role
import com.koddy.server.member.presentation.request.AuthenticationConfirmWithMailRequest
import com.koddy.server.member.presentation.request.AuthenticationWithMailRequest
import com.koddy.server.member.presentation.request.AuthenticationWithProofDataRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "2-5. 멘토 학교 인증 API")
@RestController
@RequestMapping("/api/mentors/me/univ")
class AuthenticationMentorUnivApi(
    private val authenticationMentorUnivUseCase: AuthenticationMentorUnivUseCase,
) {
    @Operation(summary = "메일 인증 시도 Endpoint")
    @PostMapping("/mail")
    @AccessControl(role = Role.MENTOR)
    @DailyMailAuthLimit
    fun authWithMail(
        @Auth authenticated: Authenticated,
        @RequestBody @Valid request: AuthenticationWithMailRequest,
    ): ResponseEntity<Void> {
        UniversityInfo.validateDomain(authenticated, request.schoolMail)
        authenticationMentorUnivUseCase.authWithMail(request.toCommand(authenticated.id))
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "메일 인증 확인 Endpoint")
    @PostMapping("/mail/confirm")
    @AccessControl(role = Role.MENTOR)
    fun confirmMailAuthCode(
        @Auth authenticated: Authenticated,
        @RequestBody @Valid request: AuthenticationConfirmWithMailRequest,
    ): ResponseEntity<Void> {
        UniversityInfo.validateDomain(authenticated, request.schoolMail)
        authenticationMentorUnivUseCase.confirmMailAuthCode(request.toCommand(authenticated.id))
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "증명자료 인증 시도 Endpoint")
    @PostMapping("/proof-data")
    @AccessControl(role = Role.MENTOR)
    fun authWithProofData(
        @Auth authenticated: Authenticated,
        @RequestBody @Valid request: AuthenticationWithProofDataRequest,
    ): ResponseEntity<Void> {
        authenticationMentorUnivUseCase.authWithProofData(request.toCommand(authenticated.id))
        return ResponseEntity.noContent().build()
    }
}
