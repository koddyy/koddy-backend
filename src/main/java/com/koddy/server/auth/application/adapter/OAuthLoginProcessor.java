package com.koddy.server.auth.application.adapter;

import com.koddy.server.auth.domain.model.oauth.OAuthProvider;
import com.koddy.server.auth.domain.model.oauth.OAuthUserResponse;

@FunctionalInterface
public interface OAuthLoginProcessor {
    OAuthUserResponse login(
            final OAuthProvider provider,
            final String code,
            final String redirectUri,
            final String state
    );
}
