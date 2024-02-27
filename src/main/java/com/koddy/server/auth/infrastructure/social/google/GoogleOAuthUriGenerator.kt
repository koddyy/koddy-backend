package com.koddy.server.auth.infrastructure.social.google

import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import java.util.UUID

@Component
class GoogleOAuthUriGenerator(
    private val properties: GoogleOAuthProperties,
) {
    fun generate(redirectUri: String): String =
        UriComponentsBuilder
            .fromUriString(properties.authUrl)
            .queryParam("response_type", "code")
            .queryParam("client_id", properties.clientId)
            .queryParam("scope", properties.scope.joinToString(separator = " "))
            .queryParam("redirect_uri", redirectUri)
            .queryParam("state", UUID.randomUUID().toString().replace("-".toRegex(), ""))
            .build()
            .toUriString()
}
