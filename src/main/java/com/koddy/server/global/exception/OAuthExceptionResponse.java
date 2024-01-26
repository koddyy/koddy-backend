package com.koddy.server.global.exception;

import com.koddy.server.auth.domain.model.oauth.OAuthUserResponse;

public record OAuthExceptionResponse(
        String id,
        String name,
        String email,
        String profileImageUrl
) {
    public OAuthExceptionResponse(final OAuthUserResponse oAuthUserResponse) {
        this(
                oAuthUserResponse.id(),
                oAuthUserResponse.name(),
                oAuthUserResponse.email(),
                oAuthUserResponse.profileImageUrl()
        );
    }
}
