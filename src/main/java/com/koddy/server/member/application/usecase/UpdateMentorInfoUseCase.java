package com.koddy.server.member.application.usecase;

import com.koddy.server.global.annotation.KoddyWritableTransactional;
import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.global.encrypt.Encryptor;
import com.koddy.server.member.application.usecase.command.UpdateMentorBasicInfoCommand;
import com.koddy.server.member.application.usecase.command.UpdateMentorPasswordCommand;
import com.koddy.server.member.application.usecase.command.UpdateMentorScheduleCommand;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MentorRepository;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class UpdateMentorInfoUseCase {
    private final MentorRepository mentorRepository;
    private final Encryptor encryptor;

    @KoddyWritableTransactional
    public void updateBasicInfo(final UpdateMentorBasicInfoCommand command) {
        final Mentor mentor = mentorRepository.getById(command.mentorId());
        mentor.updateBasicInfo(
                command.name(),
                command.profileImageUrl(),
                command.introduction(),
                command.languages(),
                command.school(),
                command.major(),
                command.grade(),
                command.meetingUrl()
        );
    }

    @KoddyWritableTransactional
    public void updatePassword(final UpdateMentorPasswordCommand command) {
        final Mentor mentor = mentorRepository.getById(command.mentorId());
        mentor.updatePassword(command.currentPassword(), command.updatePassword(), encryptor);
    }

    @KoddyWritableTransactional
    public void updateSchedule(final UpdateMentorScheduleCommand command) {
        final Mentor mentor = mentorRepository.getById(command.mentorId());
        mentor.updateSchedules(command.schedules());
    }
}
