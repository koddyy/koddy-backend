package com.koddy.server.auth.infrastructure.social.google.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.koddy.server.auth.domain.model.oauth.OAuthUserResponse

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
data class GoogleUserResponse(
    val sub: String,
    val name: String,
    val givenName: String?,
    val familyName: String?,
    val picture: String,
    val email: String,
    val emailVerified: Boolean,
    val locale: String?,
) : OAuthUserResponse {
    override fun id(): String = sub

    override fun name(): String = name

    override fun email(): String = email

    override fun profileImageUrl(): String = picture
}
