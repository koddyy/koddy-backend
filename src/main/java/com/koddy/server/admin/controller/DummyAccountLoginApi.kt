package com.koddy.server.admin.controller

import com.koddy.server.admin.utils.NotProvidedInProduction
import com.koddy.server.auth.domain.model.AuthToken
import com.koddy.server.auth.domain.service.TokenIssuer
import com.koddy.server.auth.utils.TokenResponseWriter
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
    private val memberRepository: MemberRepository,
    private val tokenIssuer: TokenIssuer,
    private val tokenResponseWriter: TokenResponseWriter,
    @Value("\${account.dummy}") private val dummyAccountPassword: String,
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
        val member: Member<*> = memberRepository.findByPlatformEmail(Email.from(request.email))
            .orElseThrow { MemberException(MEMBER_NOT_FOUND) }

        if (dummyAccountPassword != request.password) {
            throw MemberException(MEMBER_NOT_FOUND)
        }

        val authToken: AuthToken = tokenIssuer.provideAuthorityToken(member.id, member.authority)
        tokenResponseWriter.applyToken(response, authToken)
        return ResponseEntity.ok().build()
    }
}
