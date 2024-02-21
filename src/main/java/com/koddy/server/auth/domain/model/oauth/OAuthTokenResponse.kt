package com.koddy.server.auth.domain.model.oauth

interface OAuthTokenResponse {
    fun accessToken(): String
}
