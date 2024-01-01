package com.koddy.server.auth.exception;

import com.koddy.server.auth.domain.model.oauth.OAuthUserResponse;
import lombok.Getter;

@Getter
public class OAuthUserNotFoundException extends RuntimeException {
    private final OAuthUserResponse response;

    public OAuthUserNotFoundException(final OAuthUserResponse response) {
        super();
        this.response = response;
    }
}
