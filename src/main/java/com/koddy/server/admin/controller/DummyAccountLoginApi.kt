package com.koddy.server.admin.controller

import com.koddy.server.admin.service.DummyAccountLoginService
import com.koddy.server.admin.utils.NotProvidedInProduction
import com.koddy.server.auth.domain.model.AuthMember
import com.koddy.server.member.domain.model.Email
import jakarta.validation.constraints.NotBlank
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth/dummy/login")
class DummyAccountLoginApi(
    private val dummyAccountLoginService: DummyAccountLoginService,
) {
    data class LoginRequest(
        @field:NotBlank(message = "이메일은 필수입니다.")
        val email: String,

        @field:NotBlank(message = "패스워드는 필수입니다.")
        val password: String,
    )

    @PostMapping
    @NotProvidedInProduction
    fun login(
        @RequestBody request: LoginRequest,
    ): ResponseEntity<AuthMember> {
        val authMember: AuthMember = dummyAccountLoginService.invoke(Email.from(request.email), request.password)
        return ResponseEntity.ok(authMember)
    }
}
