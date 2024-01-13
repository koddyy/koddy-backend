package com.koddy.server.file.application.usecase;

import com.koddy.server.file.application.adapter.FileManager;
import com.koddy.server.file.application.usecase.command.UploadFileCommand;
import com.koddy.server.global.annotation.UseCase;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class UploadFileUseCase {
    private final FileManager fileManager;

    public String invoke(final UploadFileCommand command) {
        return fileManager.upload(command.file());
    }
}
