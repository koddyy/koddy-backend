package com.koddy.server.auth.infrastructure.social.google;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

@ConfigurationProperties("oauth2.google")
public record GoogleOAuthProperties(
        String grantType,
        String clientId,
        String clientSecret,
        String redirectUri,
        Set<String> scope,
        String authUrl,
        String tokenUrl,
        String userInfoUrl
) {
}
