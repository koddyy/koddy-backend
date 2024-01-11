package com.koddy.server.common.mock.stub;

import com.koddy.server.auth.application.adapter.OAuthConnector;
import com.koddy.server.auth.domain.model.oauth.OAuthProvider;
import com.koddy.server.auth.domain.model.oauth.OAuthTokenResponse;
import com.koddy.server.auth.domain.model.oauth.OAuthUserResponse;
import com.koddy.server.common.fixture.OAuthFixture;

public class StubOAuthConnector implements OAuthConnector {
    @Override
    public boolean isSupported(final OAuthProvider provider) {
        return true;
    }

    @Override
    public OAuthTokenResponse fetchToken(final String code, final String redirectUri, final String state) {
        return OAuthFixture.parseOAuthTokenByCode(code);
    }

    @Override
    public OAuthUserResponse fetchUserInfo(final String accessToken) {
        return OAuthFixture.parseOAuthUserByAccessToken(accessToken);
    }
}
