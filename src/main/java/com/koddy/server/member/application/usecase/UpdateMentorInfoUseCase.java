package com.koddy.server.member.application.usecase;

import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.member.application.usecase.command.UpdateMentorBasicInfoCommand;
import com.koddy.server.member.application.usecase.command.UpdateMentorPasswordCommand;
import com.koddy.server.member.application.usecase.command.UpdateMentorScheduleCommand;
import com.koddy.server.member.domain.repository.MentorRepository;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class UpdateMentorInfoUseCase {
    private final MentorRepository mentorRepository;

    public void updateBasicInfo(final UpdateMentorBasicInfoCommand command) {

    }

    public void updatePassword(final UpdateMentorPasswordCommand command) {

    }

    public void updateSchedule(final UpdateMentorScheduleCommand command) {

    }
}
