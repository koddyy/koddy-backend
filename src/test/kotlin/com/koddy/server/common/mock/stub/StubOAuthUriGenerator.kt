package com.koddy.server.common.mock.stub

import com.koddy.server.auth.application.adapter.OAuthUriGenerator
import com.koddy.server.auth.domain.model.oauth.OAuthProvider

open class StubOAuthUriGenerator : OAuthUriGenerator {
    override fun generate(
        provider: OAuthProvider,
        redirectUri: String,
    ): String = String.format(BASE_URL, provider.value, redirectUri)

    companion object {
        const val BASE_URL: String = "https://localhost:3000/login/%s?redirectUri=%s"
    }
}
