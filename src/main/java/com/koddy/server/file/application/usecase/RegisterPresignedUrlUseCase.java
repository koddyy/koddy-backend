package com.koddy.server.file.application.usecase;

import com.koddy.server.file.application.adapter.FileManager;
import com.koddy.server.file.application.usecase.command.RegisterPresignedUrlCommand;
import com.koddy.server.file.domain.model.PresignedUrlDetails;
import com.koddy.server.global.annotation.UseCase;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class RegisterPresignedUrlUseCase {
    private final FileManager fileManager;

    public PresignedUrlDetails invoke(final RegisterPresignedUrlCommand command) {
        return fileManager.createPresignedUrl(command.file());
    }
}
