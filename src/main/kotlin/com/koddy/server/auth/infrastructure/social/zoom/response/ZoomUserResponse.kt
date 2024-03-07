package com.koddy.server.auth.infrastructure.social.zoom.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.koddy.server.auth.domain.model.oauth.OAuthUserResponse

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
data class ZoomUserResponse(
    val id: String,
    val accountId: String?,
    val accountNumber: String?,
    val firstName: String?,
    val lastName: String?,
    val displayName: String,
    val email: String,
    val roleName: String?,
    val pmi: String?,
    val personalMeetingUrl: String?,
    val timezone: String?,
    val picUrl: String,
) : OAuthUserResponse {
    override fun id(): String = id

    override fun name(): String = displayName

    override fun email(): String = email

    override fun profileImageUrl(): String = picUrl
}
