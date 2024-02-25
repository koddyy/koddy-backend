package com.koddy.server.member.application.usecase;

import com.koddy.server.global.annotation.KoddyWritableTransactional;
import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.member.application.usecase.command.UpdateMenteeBasicInfoCommand;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.repository.MenteeRepository;

@UseCase
public class UpdateMenteeInfoUseCase {
    private final MenteeRepository menteeRepository;

    public UpdateMenteeInfoUseCase(final MenteeRepository menteeRepository) {
        this.menteeRepository = menteeRepository;
    }

    @KoddyWritableTransactional
    public void updateBasicInfo(final UpdateMenteeBasicInfoCommand command) {
        final Mentee mentee = menteeRepository.getById(command.menteeId());
        mentee.updateBasicInfo(
                command.name(),
                command.nationality(),
                command.profileImageUrl(),
                command.introduction(),
                command.languages(),
                command.interestSchool(),
                command.interestMajor()
        );
    }
}
