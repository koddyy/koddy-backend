package com.koddy.server.file.application.usecase.command;

import com.koddy.server.file.domain.model.RawFileData;

public record UploadFileCommand(
        RawFileData file
) {
}
