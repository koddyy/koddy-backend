package com.koddy.server.auth.infrastructure.social.kakao

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "oauth2.kakao")
data class KakaoOAuthProperties(
    val grantType: String,
    val clientId: String,
    val clientSecret: String,
    val redirectUri: String,
    val scope: Set<String> = emptySet(),
    val authUrl: String,
    val tokenUrl: String,
    val userInfoUrl: String,
)
