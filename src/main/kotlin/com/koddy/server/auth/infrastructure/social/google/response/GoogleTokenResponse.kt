package com.koddy.server.auth.infrastructure.social.google.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.koddy.server.auth.domain.model.oauth.OAuthTokenResponse

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
data class GoogleTokenResponse(
    val tokenType: String,
    val idToken: String,
    val accessToken: String,
    val refreshToken: String?,
    val expiresIn: Long,
) : OAuthTokenResponse {
    override fun accessToken(): String = accessToken
}
