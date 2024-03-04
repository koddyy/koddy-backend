package com.koddy.server.member.presentation

import com.koddy.server.auth.domain.model.AuthToken
import com.koddy.server.auth.domain.service.TokenIssuer
import com.koddy.server.auth.utils.TokenResponseWriter
import com.koddy.server.global.annotation.UseCase
import com.koddy.server.global.aop.NotProvidedInProduction
import com.koddy.server.member.domain.model.Email
import com.koddy.server.member.domain.model.Member
import com.koddy.server.member.domain.repository.MemberRepository
import com.koddy.server.member.exception.MemberException
import com.koddy.server.member.exception.MemberExceptionCode.MEMBER_NOT_FOUND
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.constraints.NotBlank
import org.springframework.beans.factory.annotation.Value
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
        val authToken: AuthToken = dummyAccountLoginUseCase.invoke(
            email = Email.from(request.email),
            password = request.password,
        )
        tokenResponseWriter.applyToken(response, authToken)
        return ResponseEntity.ok().build()
    }
}

@UseCase
class DummyAccountLoginUseCase(
    private val memberRepository: MemberRepository,
    private val tokenIssuer: TokenIssuer,
    @Value("\${account.dummy}") private val dummyAccountPassword: String,
) {
    fun invoke(
        email: Email,
        password: String,
    ): AuthToken {
        val member: Member<*> = memberRepository.findByPlatformEmail(email)
            .orElseThrow { MemberException(MEMBER_NOT_FOUND) }

        if (dummyAccountPassword != password) {
            throw MemberException(MEMBER_NOT_FOUND)
        }

        return tokenIssuer.provideAuthorityToken(member.id, member.authority)
    }
}
