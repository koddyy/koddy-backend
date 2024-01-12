package com.koddy.server.auth.application.adapter;

import com.koddy.server.auth.domain.model.oauth.OAuthProvider;

@FunctionalInterface
public interface OAuthUriGenerator {
    String generate(final OAuthProvider provider, final String redirectUri);
}
