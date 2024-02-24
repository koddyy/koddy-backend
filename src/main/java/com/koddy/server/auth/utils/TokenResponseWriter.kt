package com.koddy.server.auth.utils

import com.koddy.server.auth.domain.model.AuthToken
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.server.Cookie.SameSite.NONE
import org.springframework.http.HttpHeaders.SET_COOKIE
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Component

@Component
class TokenResponseWriter(
    @Value("\${jwt.refresh-token-validity-seconds}") private val refreshTokenCookieAge: Long,
) {
    fun applyToken(
        response: HttpServletResponse,
        token: AuthToken,
    ) {
        applyAccessToken(response, token.accessToken)
        applyRefreshToken(response, token.refreshToken)
    }

    fun expireRefreshTokenCookie(response: HttpServletResponse) {
        val cookie: ResponseCookie = ResponseCookie.from(AuthToken.REFRESH_TOKEN_HEADER, "")
            .maxAge(1)
            .sameSite(NONE.attributeValue())
            .secure(true)
            .httpOnly(true)
            .path("/")
            .build()
        response.setHeader(SET_COOKIE, cookie.toString())
    }

    private fun applyAccessToken(
        response: HttpServletResponse,
        accessToken: String,
    ) {
        response.setHeader(AuthToken.ACCESS_TOKEN_HEADER, "${AuthToken.TOKEN_TYPE} $accessToken")
    }

    private fun applyRefreshToken(
        response: HttpServletResponse,
        refreshToken: String,
    ) {
        val cookie: ResponseCookie = ResponseCookie.from(AuthToken.REFRESH_TOKEN_HEADER, refreshToken)
            .maxAge(refreshTokenCookieAge)
            .sameSite(NONE.attributeValue())
            .secure(true)
            .httpOnly(true)
            .path("/")
            .build()
        response.setHeader(SET_COOKIE, cookie.toString())
    }
}
