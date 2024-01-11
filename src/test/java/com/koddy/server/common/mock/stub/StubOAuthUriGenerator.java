package com.koddy.server.common.mock.stub;

import com.koddy.server.auth.application.adapter.OAuthUriGenerator;
import com.koddy.server.auth.domain.model.oauth.OAuthProvider;

public class StubOAuthUriGenerator implements OAuthUriGenerator {
    @Override
    public boolean isSupported(final OAuthProvider provider) {
        return true;
    }

    @Override
    public String generate(final String redirectUri) {
        return "https://localhost:3000";
    }
}
