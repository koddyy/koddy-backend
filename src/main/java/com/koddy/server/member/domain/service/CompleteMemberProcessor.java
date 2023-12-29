package com.koddy.server.member.domain.service;

import com.koddy.server.global.annotation.KoddyWritableTransactional;
import com.koddy.server.member.application.usecase.command.CompleteMenteeCommand;
import com.koddy.server.member.application.usecase.command.CompleteMentorCommand;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MenteeRepository;
import com.koddy.server.member.domain.repository.MentorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompleteMemberProcessor {
    private final MentorRepository mentorRepository;
    private final MenteeRepository menteeRepository;

    @KoddyWritableTransactional
    public void completeMentor(final CompleteMentorCommand command, final String profileImageUrl) {
        final Mentor mentor = mentorRepository.getById(command.mentorId());
        mentor.complete(
                command.name(),
                command.nationality(),
                profileImageUrl,
                command.languages(),
                command.universityProfile(),
                command.meetingUrl(),
                command.introduction(),
                command.schedules()
        );
    }

    @KoddyWritableTransactional
    public void completeMentee(final CompleteMenteeCommand command, final String profileImageUrl) {
        final Mentee mentee = menteeRepository.getById(command.menteeId());
        mentee.complete(
                command.name(),
                command.nationality(),
                profileImageUrl,
                command.languages(),
                command.interest()
        );
    }
}
