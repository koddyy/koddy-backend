package com.koddy.server.common.mock.stub;

import com.koddy.server.auth.application.adapter.OAuthLoginProcessor;
import com.koddy.server.auth.domain.model.oauth.OAuthProvider;
import com.koddy.server.auth.domain.model.oauth.OAuthTokenResponse;
import com.koddy.server.auth.domain.model.oauth.OAuthUserResponse;
import com.koddy.server.common.fixture.OAuthFixture;

public class StubOAuthLoginProcessor implements OAuthLoginProcessor {
    @Override
    public OAuthUserResponse login(final OAuthProvider provider, final String code, final String redirectUri, final String state) {
        final OAuthTokenResponse token = OAuthFixture.parseOAuthTokenByCode(code);
        return OAuthFixture.parseOAuthUserByAccessToken(token.accessToken());
    }
}
