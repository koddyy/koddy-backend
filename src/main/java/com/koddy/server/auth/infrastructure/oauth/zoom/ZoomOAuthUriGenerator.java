package com.koddy.server.auth.infrastructure.oauth.zoom;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ZoomOAuthUriGenerator {
    private final ZoomOAuthProperties properties;

    public String generate(final String redirectUri) {
        return UriComponentsBuilder
                .fromUriString(properties.authUrl())
                .queryParam("response_type", "code")
                .queryParam("client_id", properties.clientId())
                .queryParam("redirect_uri", redirectUri)
                .queryParam("state", UUID.randomUUID().toString().replaceAll("-", ""))
                .build()
                .toUriString();
    }
}
