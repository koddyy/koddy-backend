package com.koddy.server.auth.presentation.request

import com.koddy.server.auth.application.usecase.command.OAuthLoginCommand
import com.koddy.server.auth.domain.model.oauth.OAuthProvider
import jakarta.validation.constraints.NotBlank

data class OAuthLoginRequest(
    @field:NotBlank(message = "Authorization Code는 필수입니다.")
    val authorizationCode: String,

    @field:NotBlank(message = "Redirect Uri는 필수입니다.")
    val redirectUri: String,

    @field:NotBlank(message = "State값은 필수입니다.")
    val state: String,
) {
    fun toCommand(provider: String): OAuthLoginCommand =
        OAuthLoginCommand(
            provider = OAuthProvider.from(provider),
            code = authorizationCode,
            redirectUrl = redirectUri,
            state = state,
        )
}
