package com.koddy.server.member.application.usecase;

import com.koddy.server.auth.domain.model.AuthMember;
import com.koddy.server.auth.domain.model.AuthToken;
import com.koddy.server.auth.domain.service.TokenIssuer;
import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.member.application.usecase.command.SignUpMenteeCommand;
import com.koddy.server.member.application.usecase.command.SignUpMentorCommand;
import com.koddy.server.member.domain.model.Member;
import com.koddy.server.member.domain.model.SocialPlatform;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MemberRepository;
import com.koddy.server.member.exception.MemberException;

import static com.koddy.server.member.exception.MemberExceptionCode.ACCOUNT_ALREADY_EXISTS;

@UseCase
public class SignUpUsecase {
    private final MemberRepository memberRepository;
    private final TokenIssuer tokenIssuer;

    public SignUpUsecase(
            final MemberRepository memberRepository,
            final TokenIssuer tokenIssuer
    ) {
        this.memberRepository = memberRepository;
        this.tokenIssuer = tokenIssuer;
    }

    public AuthMember signUpMentor(final SignUpMentorCommand command) {
        validateAccountExists(command.platform());
        final Mentor mentor = memberRepository.save(command.toDomain());
        return provideAuthMember(mentor);
    }

    public AuthMember signUpMentee(final SignUpMenteeCommand command) {
        validateAccountExists(command.platform());
        final Mentee mentee = memberRepository.save(command.toDomain());
        return provideAuthMember(mentee);
    }

    private void validateAccountExists(final SocialPlatform platform) {
        if (memberRepository.existsByPlatformSocialId(platform.getSocialId())) {
            throw new MemberException(ACCOUNT_ALREADY_EXISTS);
        }
    }

    private AuthMember provideAuthMember(final Member<?> member) {
        final AuthToken authToken = tokenIssuer.provideAuthorityToken(member.getId(), member.getAuthority());
        return new AuthMember(member, authToken);
    }
}
