package com.koddy.server.auth.application.adapter

import com.koddy.server.auth.domain.model.oauth.OAuthProvider
import com.koddy.server.auth.domain.model.oauth.OAuthUserResponse

fun interface OAuthLoginProcessor {
    fun login(
        provider: OAuthProvider,
        code: String,
        redirectUri: String,
        state: String,
    ): OAuthUserResponse
}
