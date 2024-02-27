package com.koddy.server.auth.application.usecase.command

import com.koddy.server.auth.domain.model.oauth.OAuthProvider

data class OAuthLoginCommand(
    val provider: OAuthProvider,
    val code: String,
    val redirectUrl: String,
    val state: String,
)
