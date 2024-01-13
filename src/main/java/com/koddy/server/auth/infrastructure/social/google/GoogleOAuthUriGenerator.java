package com.koddy.server.auth.infrastructure.social.google;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GoogleOAuthUriGenerator {
    private final GoogleOAuthProperties properties;

    public String generate(final String redirectUri) {
        return UriComponentsBuilder
                .fromUriString(properties.authUrl())
                .queryParam("response_type", "code")
                .queryParam("client_id", properties.clientId())
                .queryParam("scope", String.join(" ", properties.scope()))
                .queryParam("redirect_uri", redirectUri)
                .queryParam("state", UUID.randomUUID().toString().replaceAll("-", ""))
                .build()
                .toUriString();
    }
}
