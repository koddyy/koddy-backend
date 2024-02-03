package com.koddy.server.auth.application.usecase;

import com.koddy.server.auth.application.adapter.OAuthLoginProcessor;
import com.koddy.server.auth.application.usecase.command.OAuthLoginCommand;
import com.koddy.server.auth.domain.model.AuthMember;
import com.koddy.server.auth.domain.model.AuthToken;
import com.koddy.server.auth.domain.model.oauth.OAuthUserResponse;
import com.koddy.server.auth.domain.service.TokenIssuer;
import com.koddy.server.auth.exception.OAuthUserNotFoundException;
import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.member.domain.model.Member;
import com.koddy.server.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class OAuthLoginUseCase {
    private final OAuthLoginProcessor oAuthLoginProcessor;
    private final MemberRepository memberRepository;
    private final TokenIssuer tokenIssuer;

    public AuthMember invoke(final OAuthLoginCommand command) {
        final OAuthUserResponse oAuthUser = oAuthLoginProcessor.login(command.provider(), command.code(), command.redirectUrl(), command.state());
        final Member<?> member = getMemberBySocialId(oAuthUser);
        final AuthToken authToken = tokenIssuer.provideAuthorityToken(member.getId(), member.getAuthority());
        return new AuthMember(member, authToken);
    }

    private Member<?> getMemberBySocialId(final OAuthUserResponse oAuthUser) {
        return memberRepository.findByPlatformSocialId(oAuthUser.id())
                .orElseThrow(() -> new OAuthUserNotFoundException(oAuthUser));
    }
}
