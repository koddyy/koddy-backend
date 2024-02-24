package com.koddy.server.auth.utils

import com.koddy.server.auth.domain.model.AuthToken
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest

object TokenExtractor {
    fun extractAccessToken(request: HttpServletRequest): String? {
        val token: String? = request.getHeader(AuthToken.ACCESS_TOKEN_HEADER)
        if (token.isNullOrEmpty()) {
            return null
        }
        return checkToken(token.split(" "))
    }

    fun extractRefreshToken(request: HttpServletRequest): String? {
        val cookies: Array<Cookie>? = request.cookies
        if (cookies.isNullOrEmpty()) {
            return null
        }
        return cookies.filter { it.name == AuthToken.REFRESH_TOKEN_HEADER }
            .map { it.value }
            .firstOrNull()
    }

    private fun checkToken(parts: List<String>): String? {
        if (parts.size == 2 && parts[0] == AuthToken.TOKEN_TYPE) {
            return parts[1]
        }
        return null
    }
}
