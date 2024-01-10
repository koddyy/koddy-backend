package com.koddy.server.member.application.usecase;

import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.member.application.usecase.command.AuthenticationConfirmWithMailCommand;
import com.koddy.server.member.application.usecase.command.AuthenticationWithMailCommand;
import com.koddy.server.member.application.usecase.command.AuthenticationWithProofDataCommand;
import com.koddy.server.member.domain.repository.MentorRepository;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class AuthenticationMentorUnivUseCase {
    private final MentorRepository mentorRepository;

    public void processWithMail(final AuthenticationWithMailCommand command) {

    }

    public void processWithProofData(final AuthenticationWithProofDataCommand command) {

    }

    public void confirmMailAuthCode(final AuthenticationConfirmWithMailCommand command) {

    }
}
