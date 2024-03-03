package com.koddy.server.auth.infrastructure.social.kakao

import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import java.util.UUID

@Component
class KakaoOAuthUriGenerator(
    private val properties: KakaoOAuthProperties,
) {
    fun generate(redirectUri: String): String {
        return UriComponentsBuilder
            .fromUriString(properties.authUrl)
            .queryParam("response_type", "code")
            .queryParam("client_id", properties.clientId)
            .queryParam("scope", properties.scope.joinToString(separator = " "))
            .queryParam("redirect_uri", redirectUri)
            .queryParam("state", UUID.randomUUID().toString().replace("-".toRegex(), ""))
            .build()
            .toUriString()
    }
}
