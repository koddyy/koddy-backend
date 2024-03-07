package com.koddy.server.auth.infrastructure.social.kakao.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.koddy.server.auth.domain.model.oauth.OAuthTokenResponse

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
data class KakaoTokenResponse(
    val tokenType: String,
    val accessToken: String,
    val refreshToken: String?,
    val refreshTokenExpiresIn: Long?,
    val expiresIn: Long,
) : OAuthTokenResponse {
    override fun accessToken(): String = accessToken
}
