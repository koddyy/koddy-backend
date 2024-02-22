package com.koddy.server.file.application.usecase

import com.koddy.server.file.application.adapter.FileManager
import com.koddy.server.file.application.usecase.command.UploadFileCommand
import com.koddy.server.global.annotation.UseCase

@UseCase
class UploadFileUseCase(
    private val fileManager: FileManager,
) {
    fun invoke(command: UploadFileCommand): String = fileManager.upload(command.file)
}
