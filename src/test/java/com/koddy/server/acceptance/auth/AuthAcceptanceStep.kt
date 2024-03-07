package com.koddy.server.acceptance.auth

import com.koddy.server.acceptance.RequestHelper
import com.koddy.server.auth.presentation.request.OAuthLoginRequest
import io.restassured.response.ValidatableResponse

object AuthAcceptanceStep {
    fun Google_OAuth_인증_URL를_생성한다(
        oAuthProvider: String,
        redirectUri: String,
    ): ValidatableResponse {
        return RequestHelper.getRequest(
            uri = "/api/oauth/access/$oAuthProvider?redirectUri=$redirectUri",
        )
    }

    fun Google_OAuth_로그인을_진행한다(
        oAuthProvider: String,
        authorizationCode: String,
        redirectUri: String,
        state: String,
    ): ValidatableResponse {
        return RequestHelper.postRequest(
            uri = "/api/oauth/login/$oAuthProvider",
            body = OAuthLoginRequest(
                authorizationCode = authorizationCode,
                redirectUri = redirectUri,
                state = state,
            ),
        )
    }

    fun 로그아웃을_진행한다(accessToken: String): ValidatableResponse {
        return RequestHelper.postRequestWithAccessToken(
            uri = "/api/oauth/logout",
            accessToken = accessToken,
        )
    }

    fun 토큰을_재발급받는다(refreshToken: String): ValidatableResponse {
        return RequestHelper.postRequestWithRefreshToken(
            uri = "/api/token/reissue",
            refreshToken = refreshToken,
        )
    }
}
