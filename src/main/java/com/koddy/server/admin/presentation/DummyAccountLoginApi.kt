package com.koddy.server.admin.presentation

import com.koddy.server.admin.application.usecase.DummyAccountLoginUseCase
import com.koddy.server.admin.utils.NotProvidedInProduction
import com.koddy.server.auth.domain.model.AuthToken
import com.koddy.server.auth.utils.TokenResponseWriter
import com.koddy.server.member.domain.model.Email
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.constraints.NotBlank
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth/dummy/login")
class DummyAccountLoginApi(
    private val dummyAccountLoginUseCase: DummyAccountLoginUseCase,
    private val tokenResponseWriter: TokenResponseWriter,
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
        response: HttpServletResponse,
    ): ResponseEntity<Void> {
        val authToken: AuthToken = dummyAccountLoginUseCase.invoke(Email.from(request.email), request.password)
        tokenResponseWriter.applyToken(response, authToken)
        return ResponseEntity.ok().build()
    }
}
