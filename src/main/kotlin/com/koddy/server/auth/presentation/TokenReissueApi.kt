package com.koddy.server.auth.presentation

import com.koddy.server.auth.application.usecase.ReissueTokenUseCase
import com.koddy.server.auth.domain.model.AuthToken
import com.koddy.server.auth.domain.model.TokenType
import com.koddy.server.auth.utils.TokenResponseWriter
import com.koddy.server.global.annotation.ExtractToken
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "1-2. 토큰 재발급 API")
@RestController
@RequestMapping("/api/token/reissue")
class TokenReissueApi(
    private val reissueTokenUseCase: ReissueTokenUseCase,
    private val tokenResponseWriter: TokenResponseWriter,
) {
    @Operation(summary = "RefreshToken을 통한 토큰 재발급 Endpoint")
    @PostMapping
    fun reissueToken(
        @ExtractToken(tokenType = TokenType.REFRESH) refreshToken: String,
        response: HttpServletResponse,
    ): ResponseEntity<Void> {
        val authToken: AuthToken = reissueTokenUseCase.invoke(refreshToken)
        tokenResponseWriter.applyToken(response, authToken)
        return ResponseEntity.noContent().build()
    }
}
