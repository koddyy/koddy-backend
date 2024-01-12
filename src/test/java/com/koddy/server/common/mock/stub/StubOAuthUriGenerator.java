package com.koddy.server.common.mock.stub;

import com.koddy.server.auth.application.adapter.OAuthUriGenerator;
import com.koddy.server.auth.domain.model.oauth.OAuthProvider;

public class StubOAuthUriGenerator implements OAuthUriGenerator {
    public static final String BASE_URL = "https://localhost:3000/%s?redirectUri=%s";

    @Override
    public String generate(final OAuthProvider provider, final String redirectUri) {
        return String.format(BASE_URL, provider.getValue(), redirectUri);
    }
}
