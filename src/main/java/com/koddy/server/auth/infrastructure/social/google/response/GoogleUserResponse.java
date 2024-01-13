package com.koddy.server.auth.infrastructure.social.google.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.koddy.server.auth.domain.model.oauth.OAuthUserResponse;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GoogleUserResponse(
        String sub,
        String name,
        String givenName,
        String familyName,
        String picture,
        String email,
        boolean emailVerified,
        String locale
) implements OAuthUserResponse {
    @Override
    public String profileImageUrl() {
        return picture;
    }
}
