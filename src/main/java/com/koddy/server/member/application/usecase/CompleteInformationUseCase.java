package com.koddy.server.member.application.usecase;

import com.koddy.server.global.annotation.KoddyWritableTransactional;
import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.member.application.usecase.command.CompleteMenteeCommand;
import com.koddy.server.member.application.usecase.command.CompleteMentorCommand;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MenteeRepository;
import com.koddy.server.member.domain.repository.MentorRepository;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class CompleteInformationUseCase {
    private final MentorRepository mentorRepository;
    private final MenteeRepository menteeRepository;

    @KoddyWritableTransactional
    public void completeMentor(final CompleteMentorCommand command) {
        final Mentor mentor = mentorRepository.getById(command.mentorId());
        mentor.complete(
                command.name(),
                command.nationality(),
                command.profileUploadUrl(),
                command.languages(),
                command.universityProfile(),
                command.meetingUrl(),
                command.introduction(),
                command.schedules()
        );
    }

    @KoddyWritableTransactional
    public void completeMentee(final CompleteMenteeCommand command) {
        final Mentee mentee = menteeRepository.getById(command.menteeId());
        mentee.complete(
                command.name(),
                command.nationality(),
                command.profileUploadUrl(),
                command.languages(),
                command.interest()
        );
    }
}
