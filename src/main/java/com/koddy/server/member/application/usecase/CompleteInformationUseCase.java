package com.koddy.server.member.application.usecase;

import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.member.application.usecase.command.CompleteMenteeCommand;
import com.koddy.server.member.application.usecase.command.CompleteMentorCommand;
import com.koddy.server.member.domain.repository.MenteeRepository;
import com.koddy.server.member.domain.repository.MentorRepository;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class CompleteInformationUseCase {
    private final MentorRepository mentorRepository;
    private final MenteeRepository menteeRepository;

    public void completMentor(final CompleteMentorCommand command) {

    }

    public void completeMentee(final CompleteMenteeCommand command) {

    }
}
