package com.koddy.server.auth.application.usecase;

import com.koddy.server.auth.application.adapter.OAuthUriGenerator;
import com.koddy.server.auth.application.usecase.query.GetOAuthLink;
import com.koddy.server.auth.exception.AuthException;
import com.koddy.server.global.annotation.UseCase;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.koddy.server.auth.exception.AuthExceptionCode.INVALID_OAUTH_PROVIDER;

@UseCase
@RequiredArgsConstructor
public class GetOAuthLinkUseCase {
    private final List<OAuthUriGenerator> oAuthUrisGenerators;

    public String invoke(final GetOAuthLink query) {
        final OAuthUriGenerator oAuthUriGenerator = oAuthUrisGenerators.stream()
                .filter(it -> it.isSupported(query.provider()))
                .findFirst()
                .orElseThrow(() -> new AuthException(INVALID_OAUTH_PROVIDER));

        return oAuthUriGenerator.generate(query.redirectUri());
    }
}
