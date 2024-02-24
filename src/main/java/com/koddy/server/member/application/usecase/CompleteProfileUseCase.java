package com.koddy.server.member.application.usecase;

import com.koddy.server.global.annotation.KoddyWritableTransactional;
import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.member.application.usecase.command.CompleteMenteeProfileCommand;
import com.koddy.server.member.application.usecase.command.CompleteMentorProfileCommand;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MenteeRepository;
import com.koddy.server.member.domain.repository.MentorRepository;

@UseCase
public class CompleteProfileUseCase {
    private final MentorRepository mentorRepository;
    private final MenteeRepository menteeRepository;

    public CompleteProfileUseCase(
            final MentorRepository mentorRepository,
            final MenteeRepository menteeRepository
    ) {
        this.mentorRepository = mentorRepository;
        this.menteeRepository = menteeRepository;
    }

    @KoddyWritableTransactional
    public void completeMentor(final CompleteMentorProfileCommand command) {
        final Mentor mentor = mentorRepository.getById(command.mentorId());
        mentor.completeInfo(
                command.introduction(),
                command.profileImageUrl(),
                command.mentoringPeriod(),
                command.timelines()
        );
    }

    @KoddyWritableTransactional
    public void completeMentee(final CompleteMenteeProfileCommand command) {
        final Mentee mentee = menteeRepository.getById(command.menteeId());
        mentee.completeInfo(command.introduction(), command.profileImageUrl());
    }
}
