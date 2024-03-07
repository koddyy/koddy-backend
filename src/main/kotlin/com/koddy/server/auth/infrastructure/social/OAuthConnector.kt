package com.koddy.server.auth.infrastructure.social

import com.koddy.server.auth.domain.model.oauth.OAuthTokenResponse
import com.koddy.server.auth.domain.model.oauth.OAuthUserResponse

interface OAuthConnector {
    fun fetchToken(
        code: String,
        redirectUri: String,
        state: String,
    ): OAuthTokenResponse

    fun fetchUserInfo(accessToken: String): OAuthUserResponse

    fun login(
        code: String,
        redirectUri: String,
        state: String,
    ): OAuthUserResponse {
        val token: OAuthTokenResponse = fetchToken(code, redirectUri, state)
        return fetchUserInfo(token.accessToken())
    }

    companion object {
        const val OAUTH_CONTENT_TYPE: String = "application/x-www-form-urlencoded; charset=utf-8;"
        const val BEARER_TOKEN_TYPE: String = "Bearer"
    }
}
