package com.koddy.server.auth.presentation

import com.koddy.server.auth.application.usecase.GetOAuthLinkUseCase
import com.koddy.server.auth.application.usecase.LogoutUseCase
import com.koddy.server.auth.application.usecase.OAuthLoginUseCase
import com.koddy.server.auth.application.usecase.command.LogoutCommand
import com.koddy.server.auth.application.usecase.query.GetOAuthLink
import com.koddy.server.auth.domain.model.AuthMember
import com.koddy.server.auth.domain.model.Authenticated
import com.koddy.server.auth.domain.model.oauth.OAuthProvider
import com.koddy.server.auth.presentation.request.OAuthLoginRequest
import com.koddy.server.auth.presentation.response.LoginResponse
import com.koddy.server.auth.utils.TokenResponseWriter
import com.koddy.server.global.ResponseWrapper
import com.koddy.server.global.annotation.Auth
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "1-1. OAuth 인증 관련 API")
@RestController
@RequestMapping("/api/oauth")
class OAuthApi(
    private val getOAuthLinkUseCase: GetOAuthLinkUseCase,
    private val oAuthLoginUseCase: OAuthLoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val tokenResponseWriter: TokenResponseWriter,
) {
    @Operation(summary = "Provider별 OAuth 인증을 위한 URL을 받는 EndPoint")
    @GetMapping(value = ["/access/{provider}"], params = ["redirectUri"])
    fun queryOAuthLink(
        @PathVariable provider: String,
        @RequestParam redirectUri: String,
    ): ResponseEntity<ResponseWrapper<String>> {
        val oAuthLink: String = getOAuthLinkUseCase.invoke(
            GetOAuthLink(
                provider = OAuthProvider.from(provider),
                redirectUri = redirectUri,
            ),
        )
        return ResponseEntity.ok(ResponseWrapper(oAuthLink))
    }

    @Operation(summary = "Authorization Code를 통해서 Provider별 인증을 위한 EndPoint")
    @PostMapping("/login/{provider}")
    fun login(
        @PathVariable provider: String,
        @RequestBody @Valid request: OAuthLoginRequest,
        response: HttpServletResponse,
    ): ResponseEntity<LoginResponse> {
        val authMember: AuthMember = oAuthLoginUseCase.invoke(request.toCommand(provider))
        tokenResponseWriter.applyToken(response, authMember.token)
        return ResponseEntity.ok(
            LoginResponse(
                id = authMember.id,
                name = authMember.name,
            ),
        )
    }

    @Operation(summary = "로그아웃 EndPoint")
    @PostMapping("/logout")
    fun logout(
        @Auth authenticated: Authenticated,
        response: HttpServletResponse,
    ): ResponseEntity<Void> {
        logoutUseCase.invoke(
            LogoutCommand(
                memberId = authenticated.id,
            ),
        )
        tokenResponseWriter.expireRefreshTokenCookie(response)
        return ResponseEntity.noContent().build()
    }
}
