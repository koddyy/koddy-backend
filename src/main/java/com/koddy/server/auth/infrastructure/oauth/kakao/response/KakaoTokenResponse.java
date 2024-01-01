package com.koddy.server.auth.infrastructure.oauth.kakao.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.koddy.server.auth.domain.model.oauth.OAuthTokenResponse;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoTokenResponse(
        String tokenType,
        String accessToken,
        String refreshToken,
        long refreshTokenExpiresIn,
        long expiresIn
) implements OAuthTokenResponse {
}
