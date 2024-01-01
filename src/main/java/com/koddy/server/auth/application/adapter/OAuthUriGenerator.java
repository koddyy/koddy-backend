package com.koddy.server.auth.application.adapter;

import com.koddy.server.auth.domain.model.oauth.OAuthProvider;

public interface OAuthUriGenerator {
    boolean isSupported(final OAuthProvider provider);

    String generate(final String redirectUri);
}
