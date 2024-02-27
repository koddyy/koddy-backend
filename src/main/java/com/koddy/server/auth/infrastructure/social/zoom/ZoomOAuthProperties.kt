package com.koddy.server.auth.infrastructure.social.zoom

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "oauth2.zoom")
data class ZoomOAuthProperties(
    val grantType: String,
    val clientId: String,
    val clientSecret: String,
    val redirectUri: String,
    val authUrl: String,
    val tokenUrl: String,
    val userInfoUrl: String,
    val other: Other,
)

data class Other(
    val createMeetingUrl: String,
    val deleteMeetingUrl: String,
)
