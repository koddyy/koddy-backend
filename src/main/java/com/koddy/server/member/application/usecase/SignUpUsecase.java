package com.koddy.server.member.application.usecase;

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

    public Long signUpMentor(final SignUpMentorCommand command) {
        return mentorRepository.save(new Mentor(
                command.email(),
                command.name(),
                command.profileImageUrl(),
                command.introduction(),
                command.languages(),
                command.universityProfile(),
                command.meetingUrl(),
                command.schedules()
        )).getId();
    }

    public Long signUpMentee(final SignUpMenteeCommand command) {
        return menteeRepository.save(new Mentee(
                command.email(),
                command.name(),
                command.profileImageUrl(),
                command.nationality(),
                command.introduction(),
                command.languages(),
                command.interest()
        )).getId();
    }
}
