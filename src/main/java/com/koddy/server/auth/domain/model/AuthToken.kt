package com.koddy.server.auth.domain.model

data class AuthToken(
    val accessToken: String,
    val refreshToken: String,
) {
    companion object {
        const val TOKEN_TYPE: String = "Bearer"
        const val ACCESS_TOKEN_HEADER: String = "Authorization"
        const val REFRESH_TOKEN_HEADER: String = "refresh_token"
    }
}
