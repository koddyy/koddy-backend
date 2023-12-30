package com.koddy.server.member.application.usecase;

import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.global.encrypt.Encryptor;
import com.koddy.server.member.application.usecase.command.UpdateMenteeBasicInfoCommand;
import com.koddy.server.member.application.usecase.command.UpdateMenteePasswordCommand;
import com.koddy.server.member.domain.repository.MenteeRepository;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class UpdateMenteeInfoUseCase {
    private final MenteeRepository menteeRepository;
    private final Encryptor encryptor;

    public void updateBasicInfo(final UpdateMenteeBasicInfoCommand command) {

    }

    public void updatePassword(final UpdateMenteePasswordCommand command) {

    }
}
