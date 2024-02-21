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
            return entries.stream()
                .filter { it.value == value }
                .findFirst()
                .orElseThrow { AuthException(AuthExceptionCode.INVALID_OAUTH_PROVIDER) }
        }
    }
}
