package com.koddy.server.auth.application.usecase;

import com.koddy.server.auth.application.usecase.command.LogoutCommand;
import com.koddy.server.auth.domain.service.TokenIssuer;
import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.member.domain.model.Member;
import com.koddy.server.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class LogoutUseCase {
    private final MemberRepository memberRepository;
    private final TokenIssuer tokenIssuer;

    public void invoke(final LogoutCommand command) {
        final Member<?> member = memberRepository.getById(command.memberId());
        tokenIssuer.deleteRefreshToken(member.getId());
    }
}
