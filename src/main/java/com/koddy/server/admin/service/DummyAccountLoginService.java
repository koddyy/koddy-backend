package com.koddy.server.admin.service;

import com.koddy.server.auth.domain.model.AuthMember;
import com.koddy.server.auth.domain.model.AuthToken;
import com.koddy.server.auth.domain.service.TokenIssuer;
import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.member.domain.model.Email;
import com.koddy.server.member.domain.model.Member;
import com.koddy.server.member.domain.repository.MemberRepository;
import com.koddy.server.member.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import static com.koddy.server.member.exception.MemberExceptionCode.MEMBER_NOT_FOUND;

@UseCase
@RequiredArgsConstructor
public class DummyAccountLoginService {
    private final MemberRepository memberRepository;
    private final TokenIssuer tokenIssuer;

    @Value("${account.dummy}")
    private String dummyAccountPassword;

    public AuthMember invoke(final Email email, final String password) {
        final Member<?> member = memberRepository.findByPlatformEmail(email)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

        if (!dummyAccountPassword.equals(password)) {
            throw new MemberException(MEMBER_NOT_FOUND);
        }

        final AuthToken authToken = tokenIssuer.provideAuthorityToken(member.getId());
        return new AuthMember(member, authToken);
    }
}
