package com.koddy.server.member.application.usecase;

import com.koddy.server.global.annotation.KoddyWritableTransactional;
import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.global.encrypt.Encryptor;
import com.koddy.server.member.application.usecase.command.UpdateMenteeBasicInfoCommand;
import com.koddy.server.member.application.usecase.command.UpdateMenteePasswordCommand;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.repository.MenteeRepository;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class UpdateMenteeInfoUseCase {
    private final MenteeRepository menteeRepository;
    private final Encryptor encryptor;

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

    @KoddyWritableTransactional
    public void updatePassword(final UpdateMenteePasswordCommand command) {
        final Mentee mentee = menteeRepository.getById(command.menteeId());
        mentee.updatePassword(command.currentPassword(), command.updatePassword(), encryptor);
    }
}
