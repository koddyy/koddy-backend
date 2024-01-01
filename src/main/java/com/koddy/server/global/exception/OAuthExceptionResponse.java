package com.koddy.server.global.exception;

import com.koddy.server.auth.domain.model.oauth.OAuthUserResponse;

public record OAuthExceptionResponse(
        String name,
        String email,
        String profileImageUrl
) {
    public OAuthExceptionResponse(final OAuthUserResponse oAuthUserResponse) {
        this(
                oAuthUserResponse.name(),
                oAuthUserResponse.email(),
                oAuthUserResponse.profileImageUrl()
        );
    }
}
