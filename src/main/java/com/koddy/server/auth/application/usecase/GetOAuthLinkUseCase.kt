package com.koddy.server.auth.application.usecase

import com.koddy.server.auth.application.usecase.query.GetOAuthLink
import com.koddy.server.auth.infrastructure.social.OAuthUriGeneratorFacade
import com.koddy.server.global.annotation.UseCase

@UseCase
class GetOAuthLinkUseCase(
    private val oAuthUriGeneratorFacade: OAuthUriGeneratorFacade,
) {
    fun invoke(query: GetOAuthLink): String {
        return oAuthUriGeneratorFacade.generate(
            provider = query.provider,
            redirectUri = query.redirectUri,
        )
    }
}
