package com.koddy.server.member.presentation

import com.koddy.server.auth.domain.model.AuthMember
import com.koddy.server.auth.domain.model.Authenticated
import com.koddy.server.auth.presentation.response.LoginResponse
import com.koddy.server.auth.utils.TokenResponseWriter
import com.koddy.server.global.annotation.Auth
import com.koddy.server.member.application.usecase.DeleteMemberUseCase
import com.koddy.server.member.application.usecase.SignUpUsecase
import com.koddy.server.member.presentation.request.SignUpMenteeRequest
import com.koddy.server.member.presentation.request.SignUpMentorRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "2-1. 사용자 계정 관리 (회원가입 + 로그인/탈퇴) API")
@RestController
@RequestMapping("/api")
class ManageAccountApi(
    private val signUpUsecase: SignUpUsecase,
    private val deleteMemberUseCase: DeleteMemberUseCase,
    private val tokenResponseWriter: TokenResponseWriter,
) {
    @Operation(summary = "멘토 회원가입 + 로그인 Endpoint")
    @PostMapping("/mentors")
    fun signUpMentor(
        @RequestBody @Valid request: SignUpMentorRequest,
        response: HttpServletResponse,
    ): ResponseEntity<LoginResponse> {
        val authMember: AuthMember = signUpUsecase.signUpMentor(request.toCommand())
        tokenResponseWriter.applyToken(response, authMember.token)
        return ResponseEntity.ok(
            LoginResponse(
                id = authMember.id,
                name = authMember.name,
            ),
        )
    }

    @Operation(summary = "멘티 회원가입 + 로그인 Endpoint")
    @PostMapping("/mentees")
    fun signUpMentee(
        @RequestBody @Valid request: SignUpMenteeRequest,
        response: HttpServletResponse,
    ): ResponseEntity<LoginResponse> {
        val authMember: AuthMember = signUpUsecase.signUpMentee(request.toCommand())
        tokenResponseWriter.applyToken(response, authMember.token)
        return ResponseEntity.ok(
            LoginResponse(
                id = authMember.id,
                name = authMember.name,
            ),
        )
    }

    @Operation(summary = "사용자 탈퇴 Endpoint")
    @DeleteMapping("/members")
    fun delete(
        @Auth authenticated: Authenticated,
        response: HttpServletResponse,
    ): ResponseEntity<Void> {
        deleteMemberUseCase.invoke(authenticated.id)
        tokenResponseWriter.expireRefreshTokenCookie(response)
        return ResponseEntity.noContent().build()
    }
}
