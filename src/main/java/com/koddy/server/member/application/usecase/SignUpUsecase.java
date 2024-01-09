package com.koddy.server.member.application.usecase;

import com.koddy.server.auth.domain.model.AuthMember;
import com.koddy.server.auth.domain.model.AuthToken;
import com.koddy.server.auth.domain.service.TokenIssuer;
import com.koddy.server.global.annotation.KoddyWritableTransactional;
import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.member.application.usecase.command.SignUpMenteeCommand;
import com.koddy.server.member.application.usecase.command.SignUpMentorCommand;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MenteeRepository;
import com.koddy.server.member.domain.repository.MentorRepository;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class SignUpUsecase {
    private final MentorRepository mentorRepository;
    private final MenteeRepository menteeRepository;
    private final TokenIssuer tokenIssuer;

    @KoddyWritableTransactional
    public AuthMember signUpMentor(final SignUpMentorCommand command) {
        final Mentor mentor = mentorRepository.save(new Mentor(
                command.email(),
                command.name(),
                command.profileImageUrl(),
                command.languages(),
                command.universityProfile()
        ));
        final AuthToken authToken = tokenIssuer.provideAuthorityToken(mentor.getId());
        return new AuthMember(mentor, authToken);
    }

    @KoddyWritableTransactional
    public AuthMember signUpMentee(final SignUpMenteeCommand command) {
        final Mentee mentee = menteeRepository.save(new Mentee(
                command.email(),
                command.name(),
                command.profileImageUrl(),
                command.nationality(),
                command.languages(),
                command.interest()
        ));
        final AuthToken authToken = tokenIssuer.provideAuthorityToken(mentee.getId());
        return new AuthMember(mentee, authToken);
    }
}
