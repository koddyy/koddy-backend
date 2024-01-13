package com.koddy.server.auth.infrastructure.social.kakao;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

@ConfigurationProperties("oauth2.kakao")
public record KakaoOAuthProperties(
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
