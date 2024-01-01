package com.koddy.server.auth.application.usecase.query;

import com.koddy.server.auth.domain.model.oauth.OAuthProvider;

public record GetOAuthLink(
        OAuthProvider provider,
        String redirectUri
) {
}
