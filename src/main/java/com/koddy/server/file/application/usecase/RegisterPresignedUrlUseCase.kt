package com.koddy.server.file.application.usecase

import com.koddy.server.file.application.adapter.FileManager
import com.koddy.server.file.application.usecase.command.RegisterPresignedUrlCommand
import com.koddy.server.file.domain.model.PresignedUrlDetails
import com.koddy.server.global.annotation.UseCase

@UseCase
class RegisterPresignedUrlUseCase(
    private val fileManager: FileManager,
) {
    fun invoke(command: RegisterPresignedUrlCommand): PresignedUrlDetails {
        return fileManager.createPresignedUrl(command.file)
    }
}
