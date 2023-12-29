package com.koddy.server.member.application.usecase;

import com.koddy.server.file.application.adapter.FileUploader;
import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.member.application.usecase.command.CompleteMenteeCommand;
import com.koddy.server.member.application.usecase.command.CompleteMentorCommand;
import com.koddy.server.member.domain.service.CompleteMemberProcessor;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class CompleteInformationUseCase {
    private final FileUploader fileUploader;
    private final CompleteMemberProcessor completeMemberProcessor;

    public void completeMentor(final CompleteMentorCommand command) {
        final String profileImageUrl = fileUploader.upload(command.profile());
        completeMemberProcessor.completeMentor(command, profileImageUrl);
    }

    public void completeMentee(final CompleteMenteeCommand command) {
        final String profileImageUrl = fileUploader.upload(command.profile());
        completeMemberProcessor.completeMentee(command, profileImageUrl);
    }
}
