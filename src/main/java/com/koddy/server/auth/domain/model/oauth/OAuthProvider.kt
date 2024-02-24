package com.koddy.server.auth.domain.model.oauth

import com.koddy.server.auth.exception.AuthException
import com.koddy.server.auth.exception.AuthExceptionCode

enum class OAuthProvider(
    val value: String,
) {
    GOOGLE("google"),
    KAKAO("kakao"),
    ZOOM("zoom"),
    ;

    companion object {
        @JvmStatic
        fun from(value: String): OAuthProvider {
            return entries.firstOrNull { it.value == value } ?: throw AuthException(AuthExceptionCode.INVALID_OAUTH_PROVIDER)
        }
    }
}
