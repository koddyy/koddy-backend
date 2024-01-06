package com.koddy.server.auth.infrastructure.oauth.zoom.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.koddy.server.auth.domain.model.oauth.OAuthUserResponse;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ZoomUserResponse(
        String id,
        String accountId,
        String accountNumber,
        String firstName,
        String lastName,
        String displayName,
        String email,
        String roleName,
        String pmi,
        String personalMeetingUrl,
        String timezone,
        String picUrl
) implements OAuthUserResponse {
    @Override
    public String name() {
        return displayName;
    }

    @Override
    public String email() {
        return email;
    }

    @Override
    public String profileImageUrl() {
        return picUrl;
    }
}
