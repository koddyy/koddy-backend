package com.koddy.server.file.application.usecase.command

import com.koddy.server.file.domain.model.PresignedFileData

data class RegisterPresignedUrlCommand(
    val file: PresignedFileData,
)
