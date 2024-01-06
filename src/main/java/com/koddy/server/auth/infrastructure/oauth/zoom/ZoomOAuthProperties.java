package com.koddy.server.auth.infrastructure.oauth.zoom;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("oauth2.zoom")
public record ZoomOAuthProperties(
        String grantType,
        String clientId,
        String clientSecret,
        String redirectUri,
        String authUrl,
        String tokenUrl,
        String userInfoUrl,
        Other other
) {
    public record Other(
            String createMeetingUrl,
            String deleteMeetingUrl
    ) {
    }
}
