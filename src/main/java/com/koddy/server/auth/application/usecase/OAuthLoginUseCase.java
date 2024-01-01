package com.koddy.server.auth.application.usecase;

import com.koddy.server.auth.application.adapter.OAuthConnector;
import com.koddy.server.auth.application.usecase.command.OAuthLoginCommand;
import com.koddy.server.auth.domain.model.AuthMember;
import com.koddy.server.auth.domain.model.AuthToken;
import com.koddy.server.auth.domain.model.oauth.OAuthProvider;
import com.koddy.server.auth.domain.model.oauth.OAuthTokenResponse;
import com.koddy.server.auth.domain.model.oauth.OAuthUserResponse;
import com.koddy.server.auth.domain.service.TokenIssuer;
import com.koddy.server.auth.exception.AuthException;
import com.koddy.server.auth.exception.OAuthUserNotFoundException;
import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.member.domain.model.Member;
import com.koddy.server.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.koddy.server.auth.exception.AuthExceptionCode.INVALID_OAUTH_PROVIDER;

@UseCase
@RequiredArgsConstructor
public class OAuthLoginUseCase {
    private final List<OAuthConnector> oAuthConnectors;
    private final MemberRepository memberRepository;
    private final TokenIssuer tokenIssuer;

    public AuthMember invoke(final OAuthLoginCommand command) {
        final OAuthUserResponse oAuthUser = getOAuthUser(command);
        final Member<?> member = getMemberByOAuthEmail(oAuthUser);
        final AuthToken authToken = tokenIssuer.provideAuthorityToken(member.getId());

        return new AuthMember(
                new AuthMember.MemberInfo(member),
                authToken
        );
    }

    private OAuthUserResponse getOAuthUser(final OAuthLoginCommand command) {
        final OAuthConnector oAuthConnector = getOAuthConnectorByProvider(command.provider());
        final OAuthTokenResponse oAuthToken = oAuthConnector.fetchToken(command.code(), command.redirectUrl(), command.state());

        return oAuthConnector.fetchUserInfo(oAuthToken.accessToken());
    }

    private OAuthConnector getOAuthConnectorByProvider(final OAuthProvider provider) {
        return oAuthConnectors.stream()
                .filter(oAuthConnector -> oAuthConnector.isSupported(provider))
                .findFirst()
                .orElseThrow(() -> new AuthException(INVALID_OAUTH_PROVIDER));
    }

    private Member<?> getMemberByOAuthEmail(final OAuthUserResponse oAuthUser) {
        return memberRepository.findByEmailValue(oAuthUser.email())
                .orElseThrow(() -> new OAuthUserNotFoundException(oAuthUser));
    }
}
