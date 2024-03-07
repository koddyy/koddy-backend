package com.koddy.server.auth.application.adapter

import com.koddy.server.auth.domain.model.oauth.OAuthProvider

fun interface OAuthUriGenerator {
    fun generate(
        provider: OAuthProvider,
        redirectUri: String,
    ): String
}
