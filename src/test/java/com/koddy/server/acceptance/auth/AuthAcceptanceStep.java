package com.koddy.server.acceptance.auth;

import com.koddy.server.auth.presentation.request.OAuthLoginRequest;
import io.restassured.response.ValidatableResponse;
import org.springframework.web.util.UriComponentsBuilder;

import static com.koddy.server.acceptance.CommonRequestFixture.getRequest;
import static com.koddy.server.acceptance.CommonRequestFixture.postRequest;
import static com.koddy.server.acceptance.CommonRequestFixture.postRequestWithAccessToken;
import static com.koddy.server.acceptance.CommonRequestFixture.postRequestWithRefreshToken;

public class AuthAcceptanceStep {
    public static ValidatableResponse Google_OAuth_인증_URL를_생성한다(final String oAuthProvider, final String redirectUri) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/oauth/access/{provider}?redirectUri={redirectUri}")
                .build(oAuthProvider, redirectUri)
                .getPath();

        return getRequest(uri);
    }

    public static ValidatableResponse Google_OAuth_로그인을_진행한다(
            final String oAuthProvider,
            final String authorizationCode,
            final String redirectUri,
            final String state
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/oauth/login/{provider}")
                .build(oAuthProvider)
                .getPath();

        final OAuthLoginRequest request = new OAuthLoginRequest(authorizationCode, redirectUri, state);

        return postRequest(uri, request);
    }

    public static ValidatableResponse 로그아웃을_진행한다(final String accessToken) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/oauth/logout")
                .build()
                .toUri()
                .getPath();

        return postRequestWithAccessToken(uri, accessToken);
    }

    public static ValidatableResponse 토큰을_재발급받는다(final String refreshToken) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/token/reissue")
                .build()
                .toUri()
                .getPath();

        return postRequestWithRefreshToken(uri, refreshToken);
    }
}
