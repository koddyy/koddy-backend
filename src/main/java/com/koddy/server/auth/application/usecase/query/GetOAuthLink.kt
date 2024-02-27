package com.koddy.server.auth.application.usecase.query

import com.koddy.server.auth.domain.model.oauth.OAuthProvider

data class GetOAuthLink(
    val provider: OAuthProvider,
    val redirectUri: String,
)
