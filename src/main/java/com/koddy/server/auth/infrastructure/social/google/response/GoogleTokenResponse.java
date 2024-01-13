package com.koddy.server.auth.infrastructure.social.google.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.koddy.server.auth.domain.model.oauth.OAuthTokenResponse;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GoogleTokenResponse(
        String tokenType,
        String idToken,
        String accessToken,
        String refreshToken,
        long expiresIn
) implements OAuthTokenResponse {
}
