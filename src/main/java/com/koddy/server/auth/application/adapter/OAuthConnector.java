package com.koddy.server.auth.application.adapter;

import com.koddy.server.auth.domain.model.oauth.OAuthProvider;
import com.koddy.server.auth.domain.model.oauth.OAuthTokenResponse;
import com.koddy.server.auth.domain.model.oauth.OAuthUserResponse;

public interface OAuthConnector {
    boolean isSupported(final OAuthProvider provider);

    OAuthTokenResponse fetchToken(final String code, final String redirectUri, final String state);

    OAuthUserResponse fetchUserInfo(final String accessToken);

    String OAUTH_CONTENT_TYPE = "application/x-www-form-urlencoded; charset=utf-8;";

    String BEARER_TOKEN_TYPE = "Bearer";
}
