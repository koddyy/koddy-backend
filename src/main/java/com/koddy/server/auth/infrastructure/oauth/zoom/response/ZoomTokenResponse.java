package com.koddy.server.auth.infrastructure.oauth.zoom.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.koddy.server.auth.domain.model.oauth.OAuthTokenResponse;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ZoomTokenResponse(
        String tokenType,
        String accessToken,
        String refreshToken,
        String scope,
        long expiresIn
) implements OAuthTokenResponse {
}
