package com.koddy.server.auth.application.usecase;

import com.koddy.server.auth.application.usecase.query.GetOAuthLink;
import com.koddy.server.auth.infrastructure.social.OAuthUriGeneratorFacade;
import com.koddy.server.global.annotation.UseCase;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class GetOAuthLinkUseCase {
    private final OAuthUriGeneratorFacade oAuthUriGeneratorFacade;

    public String invoke(final GetOAuthLink query) {
        return oAuthUriGeneratorFacade.generate(query.provider(), query.redirectUri());
    }
}
