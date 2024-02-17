package com.koddy.server.common.mock.stub

import com.koddy.server.auth.application.adapter.OAuthLoginProcessor
import com.koddy.server.auth.domain.model.oauth.OAuthProvider
import com.koddy.server.auth.domain.model.oauth.OAuthTokenResponse
import com.koddy.server.auth.domain.model.oauth.OAuthUserResponse
import com.koddy.server.common.fixture.OAuthFixture

open class StubOAuthLoginProcessor : OAuthLoginProcessor {
    override fun login(
        provider: OAuthProvider,
        code: String,
        redirectUri: String,
        state: String,
    ): OAuthUserResponse {
        val token: OAuthTokenResponse = OAuthFixture.parseOAuthTokenByCode(code)
        return OAuthFixture.parseOAuthUserByAccessToken(token.accessToken())
    }
}
